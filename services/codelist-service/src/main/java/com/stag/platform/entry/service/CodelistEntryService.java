package com.stag.platform.entry.service;

import com.stag.platform.entry.entity.CodelistEntry;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.exception.CodelistEntriesNotFoundException;
import com.stag.platform.entry.exception.CodelistMeaningsNotFoundException;
import com.stag.platform.entry.exception.CodelistMeaningsNotFoundException.MissingMeaning;
import com.stag.platform.entry.repository.CodelistEntryRepository;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.repository.specification.CodelistEntrySpecification;
import com.stag.platform.entry.service.dto.PersonProfileLowValues;
import com.stag.platform.shared.grpc.model.CodelistDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
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

        List<CodelistEntryId> missingIds = getMissingIds(requestedIds, entryMeanings);

        // If, after filtering, there are IDs still missing, throw an exception
        if (!missingIds.isEmpty()) {
            throw new CodelistEntriesNotFoundException(missingIds);
        }
    }

    private List<CodelistEntryId> getMissingIds(List<CodelistEntryId> requestedIds, List<CodelistEntryMeaningProjection> entryMeanings) {
        Set<CodelistEntryId> foundIds = entryMeanings.stream()
                                                     .map(CodelistEntryMeaningProjection::id)
                                                     .collect(Collectors.toSet());

        return requestedIds.stream()
                           .filter(id -> !foundIds.contains(id))
                           .toList();
    }

    @Transactional(readOnly = true)
    public PersonProfileLowValues findPersonProfileLowValues(String maritalStatus, String titlePrefix, String titleSuffix) {
        List<CodelistEntry> entries = codelistEntryRepository.findAll(
            CodelistEntrySpecification.byPersonProfileCriteria(maritalStatus, titlePrefix, titleSuffix)
        );

        List<MissingMeaning> missingMeanings = new ArrayList<>();

        String maritalStatusLowValue = findLowValueOrCollectMissing(entries, CodelistDomain.STAV, maritalStatus, missingMeanings);
        String titlePrefixLowValue = findLowValueOrCollectMissing(entries, CodelistDomain.TITUL_PRED, titlePrefix, missingMeanings);
        String titleSuffixLowValue = findLowValueOrCollectMissing(entries, CodelistDomain.TITUL_ZA, titleSuffix, missingMeanings);

        if (!missingMeanings.isEmpty()) {
            throw new CodelistMeaningsNotFoundException(missingMeanings);
        }

        return new PersonProfileLowValues(maritalStatusLowValue, titlePrefixLowValue, titleSuffixLowValue);
    }

    private String findLowValueOrCollectMissing(List<CodelistEntry> entries, CodelistDomain domain, String meaning, List<MissingMeaning> missingMeanings) {
        if (meaning == null) {
            return null;
        }

        return entries.stream()
                      .filter(e -> domain.name().equals(e.getId().getDomain()))
                      .filter(e -> meaningMatches(e, meaning))
                      .map(e -> e.getId().getLowValue())
                      .findFirst()
                      .orElseGet(() -> {
                          missingMeanings.add(new MissingMeaning(domain.name(), meaning));
                          return null;
                      });
    }

    private boolean meaningMatches(CodelistEntry entry, String meaning) {
        return meaning.equals(entry.getAbbreviation())
            || meaning.equals(entry.getMeaningCz())
            || meaning.equals(entry.getMeaningEn());
    }

}