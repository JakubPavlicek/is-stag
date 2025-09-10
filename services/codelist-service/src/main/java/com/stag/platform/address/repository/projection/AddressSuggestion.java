package com.stag.platform.address.repository.projection;

public record AddressSuggestion(
    Long addressPointId,
    String fullAddress
) {
}
