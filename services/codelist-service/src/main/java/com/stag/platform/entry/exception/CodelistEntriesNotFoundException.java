package com.stag.platform.entry.exception;

import com.stag.platform.entry.entity.CodelistEntryId;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CodelistEntriesNotFoundException extends RuntimeException {

    private final List<CodelistEntryId> missingIds;

    public CodelistEntriesNotFoundException(List<CodelistEntryId> missingIds) {
        super("Unable to find codelist entries for IDs: [" + formatMissingIdsForError(missingIds) + "]");
        this.missingIds = missingIds;
    }

    private static String formatMissingIdsForError(List<CodelistEntryId> missingIds) {
        return missingIds.stream()
                         .map(id -> id.getDomain() + ":" + id.getLowValue())
                         .collect(Collectors.joining(", "));
    }

}
