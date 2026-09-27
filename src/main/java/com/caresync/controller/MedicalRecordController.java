package com.caresync.controller;

import com.caresync.dto.MedicalRecordResponse;
import com.caresync.entity.MedicalRecord;
import com.caresync.entity.User;
import com.caresync.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/records")
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    public MedicalRecordController(
            MedicalRecordService recordService) {

        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<?> createRecord(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @RequestBody MedicalRecord record) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            MedicalRecord saved =
                    recordService.createRecord(
                            email,
                            record
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
    public ResponseEntity<?> getRecords(
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

            List<MedicalRecordResponse> records =
                    recordService.getRecords(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(records);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecord(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @PathVariable("id") Long id,
            @RequestBody MedicalRecord record) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            MedicalRecord updated =
                    recordService.updateRecord(
                            email,
                            id,
                            record
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
    public ResponseEntity<?> deleteRecord(
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

            recordService.deleteRecord(
                    email,
                    id
            );

            return ResponseEntity.ok(
                    "Medical record deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private MedicalRecordResponse toResponse(
            MedicalRecord record) {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        User patient = record.getPatient();

        response.setId(record.getId());
        response.setPatientName(patient.getName());
        response.setPatientEmail(patient.getEmail());
        response.setDoctorName(record.getDoctorName());
        response.setRecordDate(record.getRecordDate());
        response.setDiagnosis(record.getDiagnosis());
        response.setDescription(record.getDescription());
        response.setTreatment(record.getTreatment());

        return response;
    }
}