package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactInternal {
    private String email;
    private String phone;
    private String mobile;
}
