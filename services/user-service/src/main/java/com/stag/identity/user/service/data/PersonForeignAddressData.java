package com.stag.identity.user.service.data;

import com.stag.identity.user.repository.projection.ForeignAddressProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonForeignAddressData {
    ForeignAddressProjection foreignPermanentAddress;
    ForeignAddressProjection foreignTemporaryAddress;
}
