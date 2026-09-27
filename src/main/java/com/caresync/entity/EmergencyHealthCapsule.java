package com.caresync.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_health_capsules")
public class EmergencyHealthCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private User patient;

    private String bloodGroup;

    @Column(length = 2000)
    private String criticalAllergies;

    @Column(length = 2000)
    private String importantConditions;

    @Column(length = 3000)
    private String currentMedicines;

    private String emergencyContactName;

    private String emergencyContactPhone;

    @Column(length = 3000)
    private String importantMedicalInformation;

    private boolean active;

    private LocalDateTime updatedAt;

    public EmergencyHealthCapsule() {
    }

    public Long getId() {
        return id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
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

        this.importantConditions = importantConditions;
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