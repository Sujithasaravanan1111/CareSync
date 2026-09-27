package com.caresync.controller;

import com.caresync.dto.CaregiverResponse;
import com.caresync.entity.CaregiverRelationship;
import com.caresync.service.CaregiverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CaregiverController {

    private final CaregiverService caregiverService;

    public CaregiverController(
            CaregiverService caregiverService) {

        this.caregiverService = caregiverService;
    }

    @PostMapping("/api/patient/caregivers")
    public ResponseEntity<?> inviteCaregiver(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @RequestParam("caregiverEmail")
            String caregiverEmail) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            toResponse(
                                    caregiverService
                                            .inviteCaregiver(
                                                    email,
                                                    caregiverEmail
                                            )
                            )
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/api/patient/caregivers")
    public ResponseEntity<?> getMyCaregivers(
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

            List<CaregiverResponse> result =
                    caregiverService
                            .getMyCaregivers(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping(
            "/api/caregiver/invitations/{id}/accept")
    public ResponseEntity<?> acceptInvitation(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("id")
            Long id) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            return ResponseEntity.ok(
                    toResponse(
                            caregiverService
                                    .acceptInvitation(
                                            email,
                                            id
                                    )
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/api/caregiver/shared-patients")
    public ResponseEntity<?> getSharedPatients(
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

            List<CaregiverResponse> result =
                    caregiverService
                            .getSharedPatients(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/api/patient/caregivers/{id}/permissions")
public ResponseEntity<?> updatePermissions(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,

        @PathVariable("id")
        Long id,

        @RequestBody CaregiverPermissionRequest request) {

    if (email == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    try {

        return ResponseEntity.ok(
                toResponse(
                        caregiverService.updatePermissions(
                                email,
                                id,
                                request.getMedicationAccess(),
                                request.getAppointmentAccess(),
                                request.getHealthHistoryAccess(),
                                request.getReminderAccess(),
                                request.getDocumentAccess()
                        )
                )
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
    @DeleteMapping(
            "/api/patient/caregivers/{id}")
    public ResponseEntity<?> revokeCaregiver(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("id")
            Long id) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            caregiverService.revokeCaregiver(
                    email,
                    id
            );

            return ResponseEntity.ok(
                    "Caregiver access revoked successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private CaregiverResponse toResponse(
            CaregiverRelationship relationship) {

        CaregiverResponse response =
                new CaregiverResponse();

        response.setId(relationship.getId());
        response.setPatientId(
        relationship.getPatient().getId()
);

        response.setCaregiverEmail(
                relationship.getCaregiverEmail()
        );

        response.setStatus(
                relationship.getStatus()
        );

        response.setMedicationAccess(
                relationship.isMedicationAccess()
        );

        response.setAppointmentAccess(
                relationship.isAppointmentAccess()
        );

        response.setHealthHistoryAccess(
                relationship.isHealthHistoryAccess()
        );

        response.setReminderAccess(
                relationship.isReminderAccess()
        );

        response.setDocumentAccess(
                relationship.isDocumentAccess()
        );

        return response;
    }
    public static class CaregiverPermissionRequest {

    private Boolean medicationAccess;
    private Boolean appointmentAccess;
    private Boolean healthHistoryAccess;
    private Boolean reminderAccess;
    private Boolean documentAccess;

    public Boolean getMedicationAccess() {
        return medicationAccess;
    }

    public void setMedicationAccess(Boolean medicationAccess) {
        this.medicationAccess = medicationAccess;
    }

    public Boolean getAppointmentAccess() {
        return appointmentAccess;
    }

    public void setAppointmentAccess(Boolean appointmentAccess) {
        this.appointmentAccess = appointmentAccess;
    }

    public Boolean getHealthHistoryAccess() {
        return healthHistoryAccess;
    }

    public void setHealthHistoryAccess(Boolean healthHistoryAccess) {
        this.healthHistoryAccess = healthHistoryAccess;
    }

    public Boolean getReminderAccess() {
        return reminderAccess;
    }

    public void setReminderAccess(Boolean reminderAccess) {
        this.reminderAccess = reminderAccess;
    }

    public Boolean getDocumentAccess() {
        return documentAccess;
    }

    public void setDocumentAccess(Boolean documentAccess) {
        this.documentAccess = documentAccess;
    }
}
}