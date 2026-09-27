package com.caresync.dto;

public class CaregiverResponse {

    private Long id;

    private Long patientId;

    private String caregiverEmail;
    private String status;

    private boolean medicationAccess;
    private boolean appointmentAccess;
    private boolean healthHistoryAccess;
    private boolean reminderAccess;
    private boolean documentAccess;

    public CaregiverResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getCaregiverEmail() {
        return caregiverEmail;
    }

    public void setCaregiverEmail(String caregiverEmail) {
        this.caregiverEmail = caregiverEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isMedicationAccess() {
        return medicationAccess;
    }

    public void setMedicationAccess(boolean medicationAccess) {
        this.medicationAccess = medicationAccess;
    }

    public boolean isAppointmentAccess() {
        return appointmentAccess;
    }

    public void setAppointmentAccess(boolean appointmentAccess) {
        this.appointmentAccess = appointmentAccess;
    }

    public boolean isHealthHistoryAccess() {
        return healthHistoryAccess;
    }

    public void setHealthHistoryAccess(boolean healthHistoryAccess) {
        this.healthHistoryAccess = healthHistoryAccess;
    }

    public boolean isReminderAccess() {
        return reminderAccess;
    }

    public void setReminderAccess(boolean reminderAccess) {
        this.reminderAccess = reminderAccess;
    }

    public boolean isDocumentAccess() {
        return documentAccess;
    }

    public void setDocumentAccess(boolean documentAccess) {
        this.documentAccess = documentAccess;
    }
}