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

/// **Codelist Entry Service**
///
/// Manages codelist entry retrieval and validation.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class CodelistEntryService {

    private final CodelistEntryRepository codelistEntryRepository;

    /// Retrieves codelist entry meanings by their composite IDs.
    ///
    /// @param entryIds List of codelist entry IDs
    /// @param language Language code ('cs' or 'en')
    /// @return List of codelist entry meaning projections
    /// @throws CodelistEntriesNotFoundException if any IDs are missing
    @Transactional(readOnly = true)
    public List<CodelistEntryMeaningProjection> findMeaningsByIds(List<CodelistEntryId> entryIds, String language) {
        List<CodelistEntryMeaningProjection> foundEntries = codelistEntryRepository.findCodelistEntriesByIds(entryIds, language);

        ensureAllEntriesWereFound(entryIds, foundEntries);

        return foundEntries;
    }

    /// Validates that all requested codelist entries were found.
    ///
    /// @throws CodelistEntriesNotFoundException if any IDs are missing
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

    /// Identifies codelist entry IDs that were not found.
    ///
    /// @return List of missing IDs
    private List<CodelistEntryId> getMissingIds(List<CodelistEntryId> requestedIds, List<CodelistEntryMeaningProjection> entryMeanings) {
        Set<CodelistEntryId> foundIds = entryMeanings.stream()
                                                     .map(CodelistEntryMeaningProjection::id)
                                                     .collect(Collectors.toSet());

        return requestedIds.stream()
                           .filter(id -> !foundIds.contains(id))
                           .toList();
    }

    /// Finds low values for person profile codelist entries by their meanings.
    ///
    /// @param maritalStatus Marital status meaning
    /// @param titlePrefix Title prefix meaning
    /// @param titleSuffix Title suffix meaning
    /// @return Person profile low values
    /// @throws CodelistMeaningsNotFoundException if any meanings are not found
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

    /// Finds low value for a specific domain and meaning, collecting missing entries.
    ///
    /// @return Low value or null if not found
    private String findLowValueOrCollectMissing(List<CodelistEntry> entries, CodelistDomain domain, String meaning, List<MissingMeaning> missingMeanings) {
        if (meaning == null || meaning.isBlank()) {
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

    /// Checks if a codelist entry matches the given meaning.
    ///
    /// @return True if the meaning matches an abbreviation or Czech/English meaning
    private boolean meaningMatches(CodelistEntry entry, String meaning) {
        return meaning.equals(entry.getAbbreviation())
            || meaning.equals(entry.getMeaningCz())
            || meaning.equals(entry.getMeaningEn());
    }

}