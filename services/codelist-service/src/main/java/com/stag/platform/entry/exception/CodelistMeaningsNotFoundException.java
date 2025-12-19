package com.stag.platform.entry.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CodelistMeaningsNotFoundException extends RuntimeException {

    private final List<MissingMeaning> missingMeanings;

    public record MissingMeaning(
        String domain,
        String meaning
    ) implements Serializable {

    }

    public CodelistMeaningsNotFoundException(List<MissingMeaning> missingMeanings) {
        super("Unable to find codelist entries for meanings: [" + formatMissingMeaningsMessage(missingMeanings) + "]");
        this.missingMeanings = missingMeanings;
    }

    private static String formatMissingMeaningsMessage(List<MissingMeaning> missingMeanings) {
        return missingMeanings.stream()
                              .map(m -> m.domain() + ":" + m.meaning())
                              .collect(Collectors.joining(", "));
    }

}
