package com.caresync.service;

import com.caresync.dto.HealthTimelineEvent;
import com.caresync.entity.*;
import com.caresync.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HealthTimelineService {

    private final UserService userService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicationRepository medicationRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalDocumentRepository documentRepository;
    private final HealthcareInstructionRepository instructionRepository;

    public HealthTimelineService(
            UserService userService,
            MedicalRecordRepository medicalRecordRepository,
            MedicationRepository medicationRepository,
            AppointmentRepository appointmentRepository,
            MedicalDocumentRepository documentRepository,
            HealthcareInstructionRepository instructionRepository) {

        this.userService = userService;
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicationRepository = medicationRepository;
        this.appointmentRepository = appointmentRepository;
        this.documentRepository = documentRepository;
        this.instructionRepository = instructionRepository;
    }

    public List<HealthTimelineEvent> getTimeline(String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        List<HealthTimelineEvent> timeline = new ArrayList<>();

        /*
         * =========================================================
         * 1. MEDICAL RECORDS
         * =========================================================
         */

        for (MedicalRecord record :
                medicalRecordRepository.findByPatient(patient)) {

            if (record.getRecordDate() == null) {
                continue;
            }

            String diagnosis = safeText(record.getDiagnosis());

            String description =
                    buildMedicalRecordDescription(record);

            timeline.add(
                    new HealthTimelineEvent(
                            record.getRecordDate().atStartOfDay(),
                            "MEDICAL_RECORD",
                            diagnosis.isEmpty()
                                    ? "Medical Consultation"
                                    : diagnosis,
                            description
                    )
            );
        }

        /*
         * =========================================================
         * 2. MEDICATIONS
         * =========================================================
         */

        for (Medication medication :
                medicationRepository.findByPatient(patient)) {

            if (medication.getStartDate() == null) {
                continue;
            }

            String medicineName =
                    safeText(medication.getMedicineName());

            StringBuilder description =
                    new StringBuilder();

            description.append("Medication started");

            if (!medicineName.isEmpty()) {
                description.append(": ")
                        .append(medicineName);
            }

            /*
             * Add available medication information.
             * This remains descriptive and does not make
             * any medical recommendation.
             */

            if (medication.getDosage() != null) {
                description.append(" | Dosage: ")
                        .append(medication.getDosage());
            }

            if (medication.getFrequency() != null) {
                description.append(" | Frequency: ")
                        .append(medication.getFrequency());
            }

            if (medication.getEndDate() != null) {
                description.append(" | End date: ")
                        .append(medication.getEndDate());
            }

            timeline.add(
                    new HealthTimelineEvent(
                            medication.getStartDate().atStartOfDay(),
                            "MEDICATION",
                            medicineName.isEmpty()
                                    ? "Medication"
                                    : medicineName,
                            description.toString()
                    )
            );
        }

        /*
         * =========================================================
         * 3. APPOINTMENTS
         * =========================================================
         */

        for (Appointment appointment :
                appointmentRepository.findByPatient(patient)) {

            if (appointment.getAppointmentDate() == null) {
                continue;
            }

            LocalDateTime dateTime =
                    appointment.getAppointmentDate()
                            .atStartOfDay();

            if (appointment.getAppointmentTime() != null) {

                dateTime = LocalDateTime.of(
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime()
                );
            }

            String reason =
                    safeText(appointment.getReason());

            String description =
                    reason.isEmpty()
                            ? "Healthcare appointment"
                            : "Purpose: " + reason;

            timeline.add(
                    new HealthTimelineEvent(
                            dateTime,
                            "APPOINTMENT",
                            "Doctor Appointment",
                            description
                    )
            );
        }

        /*
         * =========================================================
         * 4. MEDICAL DOCUMENTS
         * =========================================================
         */

        for (MedicalDocument document :
                documentRepository.findByPatient(patient)) {

            if (document.getUploadedAt() == null) {
                continue;
            }

            String fileName =
                    safeText(document.getFileName());

            String category =
                    safeText(document.getDocumentCategory());

            String description;

            if (category.isEmpty()) {
                description = "Healthcare document uploaded";
            } else {
                description =
                        "Healthcare document uploaded | Category: "
                                + category;
            }

            timeline.add(
                    new HealthTimelineEvent(
                            document.getUploadedAt(),
                            "DOCUMENT",
                            fileName.isEmpty()
                                    ? "Medical Document"
                                    : fileName,
                            description
                    )
            );
        }

        /*
         * =========================================================
         * 5. HEALTHCARE INSTRUCTIONS
         * =========================================================
         */

        for (HealthcareInstruction instruction :
                instructionRepository.findByPatient(patient)) {

            LocalDateTime createdAt =
                    instruction.getCreatedAt();

            if (createdAt == null) {
                continue;
            }

            String instructionType =
                    safeText(instruction.getInstructionType());

            String instructionText =
                    safeText(instruction.getInstruction());

            StringBuilder description =
                    new StringBuilder();

            if (!instructionText.isEmpty()) {
                description.append(instructionText);
            }

            if (instruction.getStatus() != null) {

                if (description.length() > 0) {
                    description.append(" | ");
                }

                description.append("Status: ")
                        .append(instruction.getStatus());
            }

            timeline.add(
                    new HealthTimelineEvent(
                            createdAt,
                            "HEALTHCARE_INSTRUCTION",
                            instructionType.isEmpty()
                                    ? "Healthcare Instruction"
                                    : instructionType,
                            description.length() == 0
                                    ? "Healthcare instruction recorded"
                                    : description.toString()
                    )
            );
        }

        /*
         * =========================================================
         * 6. INTELLIGENT TIMELINE ENRICHMENT
         * =========================================================
         *
         * The original timeline simply displayed raw records.
         *
         * Here we add descriptive intelligence:
         *
         * - historical/current classification
         * - medication lifecycle information
         * - healthcare journey context
         * - chronological grouping
         *
         * No diagnosis or medical recommendation is generated.
         */

        enrichTimeline(timeline);

        /*
         * =========================================================
         * 7. CHRONOLOGICAL ORDER
         * =========================================================
         *
         * Most recent healthcare information appears first.
         */

        timeline.sort(
                Comparator.comparing(
                        HealthTimelineEvent::getDate,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                ).reversed()
        );

        return timeline;
    }

    /*
     * =============================================================
     * TIMELINE INTELLIGENCE
     * =============================================================
     */

    private void enrichTimeline(
            List<HealthTimelineEvent> timeline) {

        if (timeline.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();

        for (HealthTimelineEvent event : timeline) {

            if (event.getDate() == null) {
                continue;
            }

            LocalDate eventDate =
                    event.getDate().toLocalDate();

            String existingDescription =
                    safeText(event.getDescription());

            String timeContext;

            if (eventDate.isAfter(today)) {

                timeContext = "Upcoming healthcare event";

            } else if (eventDate.equals(today)) {

                timeContext = "Healthcare event recorded today";

            } else {

                timeContext = "Historical healthcare event";
            }

            String intelligentDescription =
                    timeContext;

            if (!existingDescription.isEmpty()) {

                intelligentDescription =
                        timeContext
                                + " | "
                                + existingDescription;
            }

            event.setDescription(
                    intelligentDescription
            );
        }
    }

    /*
     * =============================================================
     * MEDICAL RECORD INTELLIGENCE
     * =============================================================
     */

    private String buildMedicalRecordDescription(
            MedicalRecord record) {

        StringBuilder description =
                new StringBuilder();

        String doctorName =
                safeText(record.getDoctorName());

        String recordDescription =
                safeText(record.getDescription());

        String treatment =
                safeText(record.getTreatment());

        if (!doctorName.isEmpty()) {

            description.append(
                    "Doctor: "
            ).append(doctorName);
        }

        if (!recordDescription.isEmpty()) {

            if (description.length() > 0) {
                description.append(" | ");
            }

            description.append(
                    recordDescription
            );
        }

        if (!treatment.isEmpty()) {

            if (description.length() > 0) {
                description.append(" | ");
            }

            description.append(
                    "Treatment: "
            ).append(treatment);
        }

        if (description.length() == 0) {
            return "Medical record recorded";
        }

        return description.toString();
    }

    /*
     * =============================================================
     * SAFE TEXT HELPER
     * =============================================================
     */

    private String safeText(String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }
}