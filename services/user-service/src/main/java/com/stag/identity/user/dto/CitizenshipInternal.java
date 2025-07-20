package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CitizenshipInternal {
    private String country;
    private String qualifier;
}
