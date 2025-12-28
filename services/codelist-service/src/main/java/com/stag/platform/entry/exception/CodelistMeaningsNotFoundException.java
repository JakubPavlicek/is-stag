package com.stag.platform.entry.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/// **Codelist Meanings Not Found Exception**
///
/// Thrown when codelist entry lookup by domain and meaning fails.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class CodelistMeaningsNotFoundException extends RuntimeException {

    /// List of missing codelist meanings
    private final List<MissingMeaning> missingMeanings;

    /// **Missing Meaning**
    ///
    /// Represents a missing codelist meaning reference.
    ///
    /// @param domain Codelist domain
    /// @param meaning Meaning value that was not found
    public record MissingMeaning(
        String domain,
        String meaning
    ) implements Serializable {

    }

    /// Creates a new exception for missing codelist meanings.
    ///
    /// @param missingMeanings List of missing meanings
    public CodelistMeaningsNotFoundException(List<MissingMeaning> missingMeanings) {
        super("Unable to find codelist entries for meanings: [" + formatMissingMeaningsMessage(missingMeanings) + "]");
        this.missingMeanings = missingMeanings;
    }

    /// Formats missing meanings into a comma-separated string.
    ///
    /// @param missingMeanings List of missing meanings
    /// @return Formatted string of meanings (domain:meaning format)
    private static String formatMissingMeaningsMessage(List<MissingMeaning> missingMeanings) {
        return missingMeanings.stream()
                              .map(m -> m.domain() + ":" + m.meaning())
                              .collect(Collectors.joining(", "));
    }

}
