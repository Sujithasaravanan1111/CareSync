package com.caresync.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class MedicationReminderResponse {

    private Long id;
    private Long medicationId;
    private String medicineName;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private String status;
    private LocalTime snoozedUntil;

    public MedicationReminderResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalTime getSnoozedUntil() {
        return snoozedUntil;
    }

    public void setSnoozedUntil(LocalTime snoozedUntil) {
        this.snoozedUntil = snoozedUntil;
    }
}