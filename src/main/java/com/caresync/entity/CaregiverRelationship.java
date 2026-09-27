package com.caresync.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "caregiver_relationships",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"patient_id", "caregiver_email"}
        )
    }
)
public class CaregiverRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "caregiver_id")
    private User caregiver;

    @Column(name = "caregiver_email", nullable = false)
    private String caregiverEmail;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private boolean medicationAccess;

    @Column(nullable = false)
    private boolean appointmentAccess;

    @Column(nullable = false)
    private boolean healthHistoryAccess;

    @Column(nullable = false)
    private boolean reminderAccess;

    @Column(nullable = false)
    private boolean documentAccess;

    public CaregiverRelationship() {
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

    public User getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(User caregiver) {
        this.caregiver = caregiver;
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

    public void setHealthHistoryAccess(
            boolean healthHistoryAccess) {
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