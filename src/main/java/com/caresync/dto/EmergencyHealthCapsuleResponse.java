package com.caresync.dto;

import java.time.LocalDateTime;

public class EmergencyHealthCapsuleResponse {

    private Long id;
    private String patientName;
    private String bloodGroup;
    private String criticalAllergies;
    private String importantConditions;
    private String currentMedicines;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String importantMedicalInformation;
    private boolean active;
    private LocalDateTime updatedAt;

    public EmergencyHealthCapsuleResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCriticalAllergies() {
        return criticalAllergies;
    }

    public void setCriticalAllergies(String criticalAllergies) {
        this.criticalAllergies = criticalAllergies;
    }

    public String getImportantConditions() {
        return importantConditions;
    }

    public void setImportantConditions(
            String importantConditions) {
        this.importantConditions =
                importantConditions;
    }

    public String getCurrentMedicines() {
        return currentMedicines;
    }

    public void setCurrentMedicines(String currentMedicines) {
        this.currentMedicines = currentMedicines;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(
            String emergencyContactName) {
        this.emergencyContactName =
                emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(
            String emergencyContactPhone) {
        this.emergencyContactPhone =
                emergencyContactPhone;
    }

    public String getImportantMedicalInformation() {
        return importantMedicalInformation;
    }

    public void setImportantMedicalInformation(
            String importantMedicalInformation) {
        this.importantMedicalInformation =
                importantMedicalInformation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}