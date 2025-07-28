package com.stag.identity.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForeignAddress {
    private String zipCode;
    private String municipality;
    private String district;
    private String postOffice;
}
