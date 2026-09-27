package com.caresync.service;
import java.time.LocalDate;
import com.caresync.entity.Appointment;
import com.caresync.entity.HealthcareInstruction;
import com.caresync.entity.Medication;
import com.caresync.entity.User;
import com.caresync.repository.AppointmentRepository;
import com.caresync.repository.HealthcareInstructionRepository;
import com.caresync.repository.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthcareInstructionService {

    private final HealthcareInstructionRepository repository;
    private final MedicationRepository medicationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;

    public HealthcareInstructionService(
            HealthcareInstructionRepository repository,
            MedicationRepository medicationRepository,
            AppointmentRepository appointmentRepository,
            UserService userService) {

        this.repository = repository;
        this.medicationRepository = medicationRepository;
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
    }

    @Transactional
public HealthcareInstruction createInstruction(
        String email,
        Long medicationId,
        Long appointmentId,
        String doctorName,
        String instruction) {

    User patient = userService.findByEmail(email);

    if (patient == null) {
        throw new RuntimeException("Patient not found");
    }

    if (instruction == null ||
            instruction.trim().isEmpty()) {

        throw new RuntimeException(
                "Instruction is required"
        );
    }

    Medication medication = null;

    if (medicationId != null) {

        medication =
                medicationRepository.findById(
                        medicationId
                );

        if (medication == null ||
                !medication.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Medication not found"
            );
        }
    }

    Appointment appointment = null;

    if (appointmentId != null) {

        appointment =
                appointmentRepository.findById(
                        appointmentId
                );

        if (appointment == null ||
                !appointment.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Appointment not found"
            );
        }
    }

    int nextVersion = 1;

    if (medicationId != null) {

        List<HealthcareInstruction> history =
                repository.findByMedication(
                        medicationId
                );

        if (!history.isEmpty()) {

            nextVersion =
                    history.get(history.size() - 1)
                            .getVersion() + 1;
        }
    }

    HealthcareInstruction newInstruction =
            new HealthcareInstruction();

    newInstruction.setPatient(patient);

    newInstruction.setMedication(medication);

    newInstruction.setAppointment(appointment);

    newInstruction.setDoctorName(doctorName);

    newInstruction.setInstruction(
            instruction.trim()
    );

    /*
     * CARESYNC instruction metadata
     */

    newInstruction.setInstructionType(
            "GENERAL"
    );

    newInstruction.setInstructionDate(
            LocalDate.now()
    );

    newInstruction.setStartDate(
            LocalDate.now()
    );

    newInstruction.setEndDate(null);

    newInstruction.setSource(
            doctorName != null &&
            !doctorName.trim().isEmpty()
                    ? "DOCTOR"
                    : "PATIENT"
    );

    newInstruction.setVerificationStatus(
            "PENDING"
    );

    newInstruction.setStatus(
            "ACTIVE"
    );

    newInstruction.setVersion(
            nextVersion
    );

    newInstruction.setCreatedAt(
            LocalDateTime.now()
    );

    return repository.save(newInstruction);
}

    @Transactional(readOnly = true)
    public List<HealthcareInstruction> getMyInstructions(
            String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("Patient not found");
        }

        return repository.findByPatient(patient);
    }

    @Transactional(readOnly = true)
    public List<HealthcareInstruction> getMedicationHistory(
            String email,
            Long medicationId) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("Patient not found");
        }

        Medication medication =
                medicationRepository.findById(medicationId);

        if (medication == null ||
                !medication.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Medication not found"
            );
        }

        return repository.findByMedication(medicationId);
    }
    @Transactional
public HealthcareInstruction updateInstruction(
        String email,
        Long instructionId,
        String instruction,
        String instructionType,
        String startDate,
        String endDate,
        String status) {

    User patient = userService.findByEmail(email);

    if (patient == null) {
        throw new RuntimeException("Patient not found");
    }

    HealthcareInstruction existing =
            repository.findById(instructionId);

    if (existing == null) {
        throw new RuntimeException(
                "Healthcare instruction not found"
        );
    }

    if (!existing.getPatient().getId()
            .equals(patient.getId())) {

        throw new RuntimeException(
                "Healthcare instruction not found"
        );
    }

    if (instruction == null ||
            instruction.trim().isEmpty()) {

        throw new RuntimeException(
                "Instruction is required"
        );
    }

    existing.setInstruction(
            instruction.trim()
    );

    if (instructionType != null &&
            !instructionType.trim().isEmpty()) {

        existing.setInstructionType(
                instructionType.trim()
        );
    }

    if (startDate != null &&
            !startDate.trim().isEmpty()) {

        existing.setStartDate(
                LocalDate.parse(startDate)
        );
    }

    if (endDate != null &&
            !endDate.trim().isEmpty()) {

        existing.setEndDate(
                LocalDate.parse(endDate)
        );
    }

    if (status != null &&
            !status.trim().isEmpty()) {

        existing.setStatus(
                status.trim().toUpperCase()
        );
    }

    existing.setVersion(
            existing.getVersion() + 1
    );

    existing.setCreatedAt(
            LocalDateTime.now()
    );

    return repository.update(existing);
}

    public boolean detectConflict(
            String previousInstruction,
            String newInstruction) {

        if (previousInstruction == null ||
                newInstruction == null) {

            return false;
        }

        String previous =
                previousInstruction.trim().toLowerCase();

        String current =
                newInstruction.trim().toLowerCase();

        if (previous.equals(current)) {
            return false;
        }

        boolean previousBeforeFood =
                previous.contains("before food");

        boolean previousAfterFood =
                previous.contains("after food");

        boolean currentBeforeFood =
                current.contains("before food");

        boolean currentAfterFood =
                current.contains("after food");

        if ((previousBeforeFood && currentAfterFood) ||
                (previousAfterFood && currentBeforeFood)) {

            return true;
        }

        return false;
    }
}