package com.caresync.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CaregiverAppointmentResponse {

    private Long id;
    private String doctorName;
    private String hospitalOrClinic;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
    private String notes;
    private String status;
    private LocalDate followUpDate;
    private String followUpInstructions;

    public CaregiverAppointmentResponse() {
    }

    public CaregiverAppointmentResponse(
            Long id,
            String doctorName,
            String hospitalOrClinic,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String reason,
            String notes,
            String status,
            LocalDate followUpDate,
            String followUpInstructions) {

        this.id = id;
        this.doctorName = doctorName;
        this.hospitalOrClinic = hospitalOrClinic;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.notes = notes;
        this.status = status;
        this.followUpDate = followUpDate;
        this.followUpInstructions = followUpInstructions;
    }

    public Long getId() {
        return id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getHospitalOrClinic() {
        return hospitalOrClinic;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public String getFollowUpInstructions() {
        return followUpInstructions;
    }
}