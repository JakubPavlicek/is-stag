package com.stag.platform.entry.exception;

import com.stag.platform.entry.entity.CodelistEntryId;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/// **Codelist Entries Not Found Exception**
///
/// Thrown when codelist entry lookup by IDs fails.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class CodelistEntriesNotFoundException extends RuntimeException {

    /// List of codelist entry IDs that were not found
    private final List<CodelistEntryId> missingIds;

    /// Creates a new exception for missing codelist entries.
    ///
    /// @param missingIds List of codelist entry IDs that were not found
    public CodelistEntriesNotFoundException(List<CodelistEntryId> missingIds) {
        super("Unable to find codelist entries for IDs: [" + formatMissingIdsMessage(missingIds) + "]");
        this.missingIds = missingIds;
    }

    /// Formats missing IDs into a comma-separated string.
    ///
    /// @param missingIds List of missing codelist entry IDs
    /// @return Formatted string of IDs (domain:lowValue format)
    private static String formatMissingIdsMessage(List<CodelistEntryId> missingIds) {
        return missingIds.stream()
                         .map(id -> id.getDomain() + ":" + id.getLowValue())
                         .collect(Collectors.joining(", "));
    }

}
