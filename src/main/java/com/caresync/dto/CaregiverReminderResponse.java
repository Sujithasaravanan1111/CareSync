package com.caresync.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CaregiverReminderResponse {

    private Long id;
    private Long medicationId;
    private String medicineName;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private String status;
    private LocalTime snoozedUntil;

    public CaregiverReminderResponse() {
    }

    public CaregiverReminderResponse(
            Long id,
            Long medicationId,
            String medicineName,
            LocalDate reminderDate,
            LocalTime reminderTime,
            String status,
            LocalTime snoozedUntil) {

        this.id = id;
        this.medicationId = medicationId;
        this.medicineName = medicineName;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.status = status;
        this.snoozedUntil = snoozedUntil;
    }

    public Long getId() {
        return id;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public String getStatus() {
        return status;
    }

    public LocalTime getSnoozedUntil() {
        return snoozedUntil;
    }
}