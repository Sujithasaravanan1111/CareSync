package com.caresync.controller;

import com.caresync.dto.EmergencyHealthCapsuleResponse;
import com.caresync.entity.EmergencyHealthCapsule;
import com.caresync.service.EmergencyHealthCapsuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient/emergency-capsule")
public class EmergencyHealthCapsuleController {

    private final EmergencyHealthCapsuleService service;

    public EmergencyHealthCapsuleController(
            EmergencyHealthCapsuleService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdate(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @RequestParam("bloodGroup")
            String bloodGroup,

            @RequestParam("criticalAllergies")
            String criticalAllergies,

            @RequestParam("importantConditions")
            String importantConditions,

            @RequestParam("currentMedicines")
            String currentMedicines,

            @RequestParam("emergencyContactName")
            String emergencyContactName,

            @RequestParam("emergencyContactPhone")
            String emergencyContactPhone,

            @RequestParam("importantMedicalInformation")
            String importantMedicalInformation) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            EmergencyHealthCapsule capsule =
                    service.createOrUpdate(
                            email,
                            bloodGroup,
                            criticalAllergies,
                            importantConditions,
                            currentMedicines,
                            emergencyContactName,
                            emergencyContactPhone,
                            importantMedicalInformation
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(capsule));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyCapsule(
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

            return ResponseEntity.ok(
                    toResponse(
                            service.getMyCapsule(email)
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @RequestParam("active")
            boolean active) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            return ResponseEntity.ok(
                    toResponse(
                            service.setActive(
                                    email,
                                    active
                            )
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private EmergencyHealthCapsuleResponse toResponse(
            EmergencyHealthCapsule capsule) {

        EmergencyHealthCapsuleResponse response =
                new EmergencyHealthCapsuleResponse();

        response.setId(capsule.getId());

        response.setPatientName(
                capsule.getPatient().getName()
        );

        response.setBloodGroup(
                capsule.getBloodGroup()
        );

        response.setCriticalAllergies(
                capsule.getCriticalAllergies()
        );

        response.setImportantConditions(
                capsule.getImportantConditions()
        );

        response.setCurrentMedicines(
                capsule.getCurrentMedicines()
        );

        response.setEmergencyContactName(
                capsule.getEmergencyContactName()
        );

        response.setEmergencyContactPhone(
                capsule.getEmergencyContactPhone()
        );

        response.setImportantMedicalInformation(
                capsule.getImportantMedicalInformation()
        );

        response.setActive(
                capsule.isActive()
        );

        response.setUpdatedAt(
                capsule.getUpdatedAt()
        );

        return response;
    }
}