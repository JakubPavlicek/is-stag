package com.stag.platform.entry.service;

import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.exception.CodelistEntriesNotFoundException;
import com.stag.platform.entry.repository.CodelistEntryRepository;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodelistEntryService {

    private final CodelistEntryRepository codelistEntryRepository;

    @Transactional(readOnly = true)
    public List<CodelistEntryMeaningProjection> findMeaningsByIds(List<CodelistEntryId> entryIds, String language) {
        List<CodelistEntryMeaningProjection> foundEntries = codelistEntryRepository.findCodelistEntriesByIds(entryIds, language);

        ensureAllEntriesWereFound(entryIds, foundEntries);

        return foundEntries;
    }

    private void ensureAllEntriesWereFound(List<CodelistEntryId> requestedIds, List<CodelistEntryMeaningProjection> entryMeanings) {
        // If counts match, all entries were found (assumes no duplicates in requestedIds)
        if (requestedIds.size() == entryMeanings.size()) {
            return;
        }

        // Determine which IDs are missing
        List<CodelistEntryId> missingIds = getMissingIds(requestedIds, entryMeanings);

        // If, after filtering, no IDs are missing (e.g., due to duplicates in the original request), then we can return successfully
        if (missingIds.isEmpty()) {
            return;
        }

        String formattedMissingIds = formatMissingIdsForError(missingIds);
        String errorMessage = "Unable to find codelist entries for IDs: [" + formattedMissingIds + "]";

        log.warn(errorMessage);
        throw new CodelistEntriesNotFoundException(errorMessage);
    }

    private List<CodelistEntryId> getMissingIds(List<CodelistEntryId> requestedIds, List<CodelistEntryMeaningProjection> entryMeanings) {
        // Determine which IDs are missing
        Set<CodelistEntryId> foundIds = entryMeanings.stream()
                                                     .map(CodelistEntryMeaningProjection::id)
                                                     .collect(Collectors.toSet());

        return requestedIds.stream()
                           .filter(id -> !foundIds.contains(id))
                           .toList();
    }

    private String formatMissingIdsForError(List<CodelistEntryId> missingIds) {
        return missingIds.stream()
                         .map(id -> id.getDomain() + ":" + id.getLowValue())
                         .collect(Collectors.joining(", "));
    }

}
