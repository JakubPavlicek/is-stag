package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TitlesInternal {
    private String prefix;
    private String suffix;
}
