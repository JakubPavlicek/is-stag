package com.stag.platform.address.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MunicipalityPartsNotFoundException extends RuntimeException {

    private final List<Long> missingIds;

    public MunicipalityPartsNotFoundException(List<Long> missingIds) {
        super("Unable to find municipality parts for IDs: [" + formatMissingIdsMessage(missingIds) + "]");
        this.missingIds = missingIds;
    }

    private static String formatMissingIdsMessage(List<Long> missingIds) {
        return missingIds.stream()
                         .map(String::valueOf)
                         .collect(Collectors.joining(", "));
    }

}