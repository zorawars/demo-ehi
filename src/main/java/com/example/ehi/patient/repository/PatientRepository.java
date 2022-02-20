package com.example.ehi.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The {@code PatientRepository} abstracts away the persistent store used to keep patient data.
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findPatientsByFirstNameContains(String firstName, Pageable pageable);

}
