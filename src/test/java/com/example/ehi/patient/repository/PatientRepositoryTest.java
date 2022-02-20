package com.example.ehi.patient.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.SQLException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    @DisplayName("Test Spring Data JPA Repository is setup and has test data")
    public void testSetup() throws SQLException {
        assertThat(patientRepository).isNotNull();
        assertThat(patientRepository.count()).isEqualTo(6);
    }

    @ParameterizedTest
    @MethodSource("firstNameTextProvider")
    @DisplayName("Test searching for patients by first name")
    public void testSearchesByFirstName(String firstNameText, int expectedSize) {
        Page<Patient> patients = patientRepository.findPatientsByFirstNameContains(firstNameText, Pageable.ofSize(10));
        assertThat(patients).isNotNull();
        assertThat(patients.getContent()).isNotNull().hasSize(expectedSize);
    }

    static Stream<Arguments> firstNameTextProvider() {
        return Stream.of(
                Arguments.of("Ste", 2),
                Arguments.of("ste", 0),
                Arguments.of("Carol", 2),
                Arguments.of("Bruce", 1),
                Arguments.of("", 6)
        );
    }
}
