package com.stag.identity.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Addresses {
    private Address permanentResidence;
    private Address temporaryResidence;
    private ForeignAddress foreignPermanentResidence;
    private ForeignAddress foreignTemporaryResidence;
}
