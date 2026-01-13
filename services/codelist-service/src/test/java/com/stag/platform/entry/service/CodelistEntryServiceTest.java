package com.stag.platform.entry.service;

import com.stag.platform.entry.entity.CodelistEntry;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.exception.CodelistEntriesNotFoundException;
import com.stag.platform.entry.exception.CodelistMeaningsNotFoundException;
import com.stag.platform.entry.repository.CodelistEntryRepository;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.service.dto.PersonProfileLowValues;
import com.stag.platform.shared.grpc.model.CodelistDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodelistEntryServiceTest {

    @Mock
    private CodelistEntryRepository codelistEntryRepository;

    @InjectMocks
    private CodelistEntryService codelistEntryService;

    @Nested
    @DisplayName("findMeaningsByIds")
    class FindMeaningsByIds {

        @Test
        @DisplayName("should return meanings when all entries found")
        void success() {
            CodelistEntryId id1 = new CodelistEntryId("D", "V1", "STA");
            List<CodelistEntryId> ids = List.of(id1);
            List<CodelistEntryMeaningProjection> expected = List.of(
                new CodelistEntryMeaningProjection(id1, "Meaning")
            );

            when(codelistEntryRepository.findCodelistEntriesByIds(ids, "en")).thenReturn(expected);

            List<CodelistEntryMeaningProjection> result = codelistEntryService.findMeaningsByIds(ids, "en");

            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("should throw CodelistEntriesNotFoundException when some entries are missing")
        void missingEntries() {
            CodelistEntryId id1 = new CodelistEntryId("D", "V1", "STA");
            CodelistEntryId id2 = new CodelistEntryId("D", "V2", "STA");
            List<CodelistEntryId> ids = List.of(id1, id2);
            
            // Only id1 found
            List<CodelistEntryMeaningProjection> found = List.of(
                new CodelistEntryMeaningProjection(id1, "Meaning")
            );

            when(codelistEntryRepository.findCodelistEntriesByIds(ids, "en")).thenReturn(found);

            assertThatThrownBy(() -> codelistEntryService.findMeaningsByIds(ids, "en"))
                .isInstanceOf(CodelistEntriesNotFoundException.class)
                .hasMessageContaining("D:V2");
        }
    }

    @Nested
    @DisplayName("findPersonProfileLowValues")
    class FindPersonProfileLowValues {

        @Test
        @DisplayName("should return low values when all meanings match by different fields")
        void success() {
            CodelistEntry stav = CodelistEntry.builder()
                .id(new CodelistEntryId(CodelistDomain.STAV.name(), "S", "STA"))
                .abbreviation("S")
                .build();
            CodelistEntry titulPred = CodelistEntry.builder()
                .id(new CodelistEntryId(CodelistDomain.TITUL_PRED.name(), "10", "STA"))
                .meaningCz("Inženýr")
                .build();
            CodelistEntry titulZa = CodelistEntry.builder()
                .id(new CodelistEntryId(CodelistDomain.TITUL_ZA.name(), "20", "STA"))
                .meaningEn("Doctor")
                .build();

            when(codelistEntryRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(stav, titulPred, titulZa));

            PersonProfileLowValues result = codelistEntryService.findPersonProfileLowValues("S", "Inženýr", "Doctor");

            assertThat(result.maritalStatusLowValue()).isEqualTo("S");
            assertThat(result.titlePrefixLowValue()).isEqualTo("10");
            assertThat(result.titleSuffixLowValue()).isEqualTo("20");
        }

        @Test
        @DisplayName("should return null for blank inputs without calling repository with those criteria")
        void blankInputs() {
            // If all blank, specification returns disjunction, results empty
            when(codelistEntryRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

            PersonProfileLowValues result = codelistEntryService.findPersonProfileLowValues("", null, "  ");

            assertThat(result.maritalStatusLowValue()).isNull();
            assertThat(result.titlePrefixLowValue()).isNull();
            assertThat(result.titleSuffixLowValue()).isNull();
        }

        @Test
        @DisplayName("should throw CodelistMeaningsNotFoundException when some meanings are not found")
        void missingMeanings() {
            CodelistEntry stav = CodelistEntry.builder()
                .id(new CodelistEntryId(CodelistDomain.STAV.name(), "S", "STA"))
                .abbreviation("S")
                .build();

            when(codelistEntryRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(stav));

            assertThatThrownBy(() -> codelistEntryService.findPersonProfileLowValues("S", "MissingPrefix", "MissingSuffix"))
                .isInstanceOf(CodelistMeaningsNotFoundException.class)
                .satisfies(ex -> {
                    CodelistMeaningsNotFoundException e = (CodelistMeaningsNotFoundException) ex;
                    assertThat(e.getMissingMeanings()).hasSize(2);
                    assertThat(e.getMessage()).contains("TITUL_PRED:MissingPrefix");
                    assertThat(e.getMessage()).contains("TITUL_ZA:MissingSuffix");
                });
        }
    }
}
