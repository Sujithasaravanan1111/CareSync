package com.caresync.service;

import com.caresync.entity.Medication;
import com.caresync.entity.User;
import com.caresync.repository.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final UserService userService;

    public MedicationService(
            MedicationRepository medicationRepository,
            UserService userService) {

        this.medicationRepository = medicationRepository;
        this.userService = userService;
    }

    @Transactional
    public Medication createMedication(
            String email,
            Medication medication) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        if (medication.getMedicineName() == null ||
                medication.getMedicineName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Medicine name is required"
            );
        }

        if (medication.getStartDate() == null) {
            throw new RuntimeException(
                    "Start date is required"
            );
        }

        if (medication.getFrequency() == null ||
                medication.getFrequency().trim().isEmpty()) {

            throw new RuntimeException(
                    "Frequency is required"
            );
        }

        medication.setPatient(patient);

        return medicationRepository.save(medication);
    }

    @Transactional(readOnly = true)
    public List<Medication> getMedications(
            String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        return medicationRepository.findByPatient(patient);
    }

    @Transactional
    public Medication updateMedication(
            String email,
            Long id,
            Medication data) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        Medication existing =
                medicationRepository.findById(id);

        if (existing == null ||
                !existing.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Medication not found"
            );
        }

        if (data.getMedicineName() != null &&
                !data.getMedicineName().trim().isEmpty()) {

            existing.setMedicineName(
                    data.getMedicineName()
            );
        }

        if (data.getDosage() != null) {
            existing.setDosage(data.getDosage());
        }

        if (data.getDosageUnit() != null) {
            existing.setDosageUnit(data.getDosageUnit());
        }

        if (data.getFrequency() != null) {
            existing.setFrequency(data.getFrequency());
        }

        if (data.getStartDate() != null) {
            existing.setStartDate(data.getStartDate());
        }

        if (data.getEndDate() != null) {
            existing.setEndDate(data.getEndDate());
        }

        if (data.getInstructions() != null) {
            existing.setInstructions(
                    data.getInstructions()
            );
        }

        if (data.getPrescribingDoctor() != null) {
            existing.setPrescribingDoctor(
                    data.getPrescribingDoctor()
            );
        }

        if (data.getPrescriptionReference() != null) {
            existing.setPrescriptionReference(
                    data.getPrescriptionReference()
            );
        }

        return medicationRepository.update(existing);
    }

    @Transactional
    public void deleteMedication(
            String email,
            Long id) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        Medication medication =
                medicationRepository.findById(id);

        if (medication == null ||
                !medication.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Medication not found"
            );
        }

        medicationRepository.delete(medication);
    }
}