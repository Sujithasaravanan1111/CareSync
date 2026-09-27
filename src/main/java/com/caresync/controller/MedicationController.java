package com.caresync.controller;

import com.caresync.dto.MedicationResponse;
import com.caresync.entity.Medication;
import com.caresync.entity.User;
import com.caresync.service.MedicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(
            MedicationService medicationService) {

        this.medicationService = medicationService;
    }

    @PostMapping
    public ResponseEntity<?> createMedication(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @RequestBody Medication medication) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            Medication saved =
                    medicationService.createMedication(
                            email,
                            medication
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(saved));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMedications(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            List<MedicationResponse> medications =
                    medicationService
                            .getMedications(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(medications);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @PathVariable("id") Long id,
            @RequestBody Medication medication) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            Medication updated =
                    medicationService.updateMedication(
                            email,
                            id,
                            medication
                    );

            return ResponseEntity.ok(
                    toResponse(updated)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @PathVariable("id") Long id) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            medicationService.deleteMedication(
                    email,
                    id
            );

            return ResponseEntity.ok(
                    "Medication deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private MedicationResponse toResponse(
            Medication medication) {

        MedicationResponse response =
                new MedicationResponse();

        User patient = medication.getPatient();

        response.setId(medication.getId());
        response.setPatientName(patient.getName());
        response.setPatientEmail(patient.getEmail());

        response.setMedicineName(
                medication.getMedicineName()
        );

        response.setDosage(
                medication.getDosage()
        );

        response.setDosageUnit(
                medication.getDosageUnit()
        );

        response.setFrequency(
                medication.getFrequency()
        );

        response.setStartDate(
                medication.getStartDate()
        );

        response.setEndDate(
                medication.getEndDate()
        );

        response.setInstructions(
                medication.getInstructions()
        );

        response.setPrescribingDoctor(
                medication.getPrescribingDoctor()
        );

        response.setPrescriptionReference(
                medication.getPrescriptionReference()
        );

        return response;
    }
}