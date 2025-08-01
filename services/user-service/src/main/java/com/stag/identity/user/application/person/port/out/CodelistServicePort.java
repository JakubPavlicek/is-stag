package com.stag.identity.user.application.person.port.out;

import com.stag.identity.user.application.person.dto.PersonAddressData;
import com.stag.identity.user.application.person.dto.PersonBankingData;
import com.stag.identity.user.application.person.dto.PersonEducationData;
import com.stag.identity.user.application.person.dto.PersonProfileData;
import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonProfile;

/// An output port defining the contract for fetching data from the external codelist service.
public interface CodelistServicePort {

    PersonProfileData getPersonProfileData(PersonProfile personProfile);

    PersonAddressData getPersonAddressData(PersonAddress personAddress);

    PersonBankingData getPersonBankingData(PersonBank personBank);

    PersonEducationData getPersonEducationData(PersonEducation personEducation);

}
