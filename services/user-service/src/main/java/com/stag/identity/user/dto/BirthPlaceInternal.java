package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BirthPlaceInternal {
    private String city;
    private String country;
}
