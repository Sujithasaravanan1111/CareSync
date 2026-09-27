package com.caresync.dto;

import java.time.LocalDate;

public class CaregiverMedicationResponse {

    private Long id;
    private String medicineName;
    private String dosage;
    private String dosageUnit;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String instructions;
    private String prescribingDoctor;
    private String prescriptionReference;

    public CaregiverMedicationResponse() {
    }

    public CaregiverMedicationResponse(
            Long id,
            String medicineName,
            String dosage,
            String dosageUnit,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            String instructions,
            String prescribingDoctor,
            String prescriptionReference) {

        this.id = id;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.dosageUnit = dosageUnit;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructions = instructions;
        this.prescribingDoctor = prescribingDoctor;
        this.prescriptionReference = prescriptionReference;
    }

    public Long getId() {
        return id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public String getFrequency() {
        return frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getPrescribingDoctor() {
        return prescribingDoctor;
    }

    public String getPrescriptionReference() {
        return prescriptionReference;
    }
}