package com.example.ehi.patient.service;

import com.example.ehi.patient.repository.Patient;
import com.example.ehi.patient.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * This {@code PatientService} separates the access of Patient data from the web layer.
 * <p>
 * This service should not have any dependency on the protocol(s) used to access the service.
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public Page<Patient> getPatients(String firstName, Pageable pageable) {
        String safeFirstName = Objects.toString(firstName, "").trim();
        if (safeFirstName.isEmpty()) {
            return patientRepository.findAll(pageable);
        }
        Page<Patient> pagedPatients = patientRepository.findPatientsByFirstNameContains(safeFirstName, pageable);
        return pagedPatients;
    }

    @Transactional(readOnly = true)
    public Optional<Patient> getPatient(long id) {
        return patientRepository.findById(id);
    }

    @Transactional
    public Patient addPatient(Patient patient) {
        Patient newPatient = patientRepository.save(patient);
        return newPatient;
    }
}
