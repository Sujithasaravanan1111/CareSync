package com.caresync.controller;

import com.caresync.dto.MedicationReminderResponse;
import com.caresync.entity.MedicationReminder;
import com.caresync.service.MedicationReminderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/reminders")
public class MedicationReminderController {

    private final MedicationReminderService reminderService;

    public MedicationReminderController(
            MedicationReminderService reminderService) {

        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<?> createReminder(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @RequestParam("medicationId") Long medicationId,
@RequestParam("reminderDate") String reminderDate,
@RequestParam("reminderTime") String reminderTime) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            MedicationReminder reminder =
                    reminderService.createReminder(
                            email,
                            medicationId,
                            LocalDate.parse(reminderDate),
                            LocalTime.parse(reminderTime)
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(reminder));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getReminders(
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

            List<MedicationReminderResponse> reminders =
                    reminderService
                            .getReminders(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(reminders);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
public ResponseEntity<?> updateStatus(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,

        @PathVariable("id") Long id,

        @RequestBody(required = false)
        StatusUpdateRequest request) {

    if (email == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    if (request == null ||
            request.getStatus() == null ||
            request.getStatus().trim().isEmpty()) {

        return ResponseEntity
                .badRequest()
                .body("Reminder status is required");
    }

    try {

        LocalTime snoozeTime = null;

        if (request.getSnoozedUntil() != null &&
                !request.getSnoozedUntil().trim().isEmpty()) {

            snoozeTime =
                    LocalTime.parse(
                            request.getSnoozedUntil()
                    );
        }

        MedicationReminder updated =
                reminderService.updateStatus(
                        email,
                        id,
                        request.getStatus()
                                .trim()
                                .toUpperCase(),
                        snoozeTime
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
    public ResponseEntity<?> deleteReminder(
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

            reminderService.deleteReminder(
                    email,
                    id
            );

            return ResponseEntity.ok(
                    "Reminder deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private MedicationReminderResponse toResponse(
            MedicationReminder reminder) {

        MedicationReminderResponse response =
                new MedicationReminderResponse();

        response.setId(reminder.getId());

        response.setMedicationId(
                reminder.getMedication().getId()
        );

        response.setMedicineName(
                reminder.getMedication().getMedicineName()
        );

        response.setReminderDate(
                reminder.getReminderDate()
        );

        response.setReminderTime(
                reminder.getReminderTime()
        );

        response.setStatus(
                reminder.getStatus()
        );

        response.setSnoozedUntil(
                reminder.getSnoozedUntil()
        );

        return response;
    }
    @GetMapping("/adherence")
public ResponseEntity<?> getAdherence(
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
                reminderService.getAdherenceStats(email)
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
public static class StatusUpdateRequest {

    private String status;

    private String snoozedUntil;

    public StatusUpdateRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSnoozedUntil() {
        return snoozedUntil;
    }

    public void setSnoozedUntil(String snoozedUntil) {
        this.snoozedUntil = snoozedUntil;
    }
}
}