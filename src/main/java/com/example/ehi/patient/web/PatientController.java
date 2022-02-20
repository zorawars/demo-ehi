package com.example.ehi.patient.web;

import com.example.ehi.patient.repository.Patient;
import com.example.ehi.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is the main REST over HTTP web interface to the application.
 */
@RestController
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Gets list of all patients")
    @ApiResponse(
            responseCode = "200",
            description = "Found patients",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)
                    )
            }
    )
    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Patient>> patients(
            @RequestParam(name = "first_name", required = false) String firstName,
            @ParameterObject Pageable pageable) {
        Page<Patient> patients = this.patientService.getPatients(firstName, pageable);
        return ResponseEntity.ok(patients);
    }

    @Operation(summary = "Get a patient by their ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Found patient",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Patient.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            )
    })
    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable long id) {
        Optional<Patient> patient = this.patientService.getPatient(id);
        return ResponseEntity.of(patient);
    }

    @Operation(summary = "Add a new patient")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Patient was added",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Patient.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)
            )
    })
    @PostMapping("/patients")
    public ResponseEntity<Patient> addPatient(@Valid @RequestBody Patient patient) {
        Patient newPatient = this.patientService.addPatient(patient);
        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPatient.getId())
                .toUri();
        return ResponseEntity.created(resourceLocation).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidArguments(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining(","));
        return ResponseEntity.badRequest().body(message);
    }

}
