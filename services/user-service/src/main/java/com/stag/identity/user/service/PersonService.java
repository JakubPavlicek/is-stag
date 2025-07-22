package com.stag.identity.user.service;

import com.stag.identity.user.dto.CodelistEntryId;
import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.dto.TitlesInternal;
import com.stag.identity.user.entity.Person;
import com.stag.identity.user.grpc.CodelistServiceClient;
import com.stag.identity.user.grpc.StudentServiceClient;
import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    private final StudentServiceClient studentServiceClient;
    private final CodelistServiceClient codelistServiceClient;

    private Person getPersonById(Integer personId) {
        return personRepository.findById(personId)
                               .orElseThrow(() -> new IllegalArgumentException("Person with ID: " + personId + " not found"));
    }

    public PersonProfileInternal getPerson(Integer personId) {
        Person person = getPersonById(personId);

        List<String> personalNumbers = studentServiceClient.getStudentPersonalNumbers(personId);

        // Get the person's titles
        List<CodelistEntryId> titleCodelistEntryIds = List.of(
            new CodelistEntryId("TITUL_PRED", person.getTitlePrefix()),
            new CodelistEntryId("TITUL_ZA", person.getTitleSuffix())
        );

        Map<CodelistEntryId, String> codelistMeanings = codelistServiceClient.getCodelistMeanings(titleCodelistEntryIds);

        PersonProfileInternal personProfileInternal = personMapper.toPersonProfileInternal(person, personalNumbers);
        TitlesInternal titlesInternal = TitlesInternal.builder()
                                                      .prefix(codelistMeanings.get(titleCodelistEntryIds.getFirst()))
                                                      .suffix(codelistMeanings.get(titleCodelistEntryIds.getLast()))
                                                      .build();
        personProfileInternal.setTitles(titlesInternal);
        return personProfileInternal;
    }

}