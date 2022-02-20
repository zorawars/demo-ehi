package com.example.ehi.patient.service;

import com.example.ehi.patient.repository.Patient;
import com.example.ehi.patient.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    @DisplayName("Test retrieving patients")
    public void testRetrievingPatients() {
        Patient patient1 = new Patient("John", "Doe", LocalDate.of(1988, 1, 1));
        patient1.setId(101L);
        Patient patient2 = new Patient("Jane", "Doe", LocalDate.of(1988, 1, 1));
        patient2.setId(102L);

        Page<Patient> allPatients = new PageImpl<>(Arrays.asList(patient1, patient2));
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(allPatients);

        Page<Patient> patientPage = patientService.getPatients("", Pageable.ofSize(10));
        assertThat(patientPage).isNotNull();
        assertThat(patientPage.hasNext()).isFalse();
        assertThat(patientPage.getContent()).hasSize(2).contains(patient1, patient2);

        Page<Patient> searchPage = new PageImpl<>(Collections.singletonList(patient1));
        when(patientRepository.findPatientsByFirstNameContains(any(), any(Pageable.class))).thenReturn(searchPage);

        Page<Patient> searchPatientPage = patientService.getPatients("Jo", Pageable.ofSize(10));
        assertThat(searchPatientPage).isNotNull();
        assertThat(searchPatientPage.hasNext()).isFalse();
        assertThat(searchPatientPage.getContent()).hasSize(1).contains(patient1).doesNotContain(patient2);
    }

    @Test
    @DisplayName("Test retrieving an existing patient")
    public void testRetrievingPatient() {
        Patient patient = new Patient("John", "Doe", LocalDate.of(1988, 1, 1));
        patient.setId(101L);
        when(patientRepository.findById(any())).thenReturn(Optional.of(patient));

        Optional<Patient> retrievedPatient = patientService.getPatient(101L);
        assertThat(retrievedPatient).isPresent();
        assertThat(retrievedPatient.get()).satisfies(p -> assertThat(p.getId()).isSameAs(101L));
    }

    @Test
    @DisplayName("Test retrieving a non-existent patient")
    public void testRetrievingNonExistingPatient() {
        when(patientRepository.findById(any())).thenReturn(Optional.empty());

        Optional<Patient> patient = patientService.getPatient(101L);
        assertThat(patient).isEmpty();
    }

    @Test
    @DisplayName("Test adding a patient")
    public void testPatientAddition() {
        Patient patient = new Patient("John", "Doe", LocalDate.of(1988, 1, 1));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient newPatient = patientService.addPatient(patient);
        assertThat(newPatient.getFirstName()).isSameAs(patient.getFirstName());
        assertThat(newPatient.getLastName()).isSameAs(patient.getLastName());
        assertThat(newPatient.getDateOfBirth()).isSameAs(patient.getDateOfBirth());
    }
}
