package com.stag.platform.address.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CountriesNotFoundException extends RuntimeException {

    private final List<Integer> missingIds;

    public CountriesNotFoundException(List<Integer> missingIds) {
        super("Unable to find countries for IDs: [" + formatMissingIdsForError(missingIds) + "]");
        this.missingIds = missingIds;
    }

    private static String formatMissingIdsForError(List<Integer> missingIds) {
        return missingIds.stream()
                         .map(String::valueOf)
                         .collect(Collectors.joining(", "));
    }

}
