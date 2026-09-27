package com.caresync.controller;
import com.caresync.entity.Medication;
import com.caresync.repository.MedicationRepository;
import com.caresync.repository.AppointmentRepository;
import com.caresync.entity.MedicalDocument;
import java.util.List;
import com.caresync.dto.CaregiverAppointmentResponse;
import com.caresync.entity.Appointment;
import com.caresync.entity.CaregiverRelationship;
import com.caresync.entity.User;
import com.caresync.repository.CaregiverRelationshipRepository;
import com.caresync.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.caresync.dto.CaregiverDocumentResponse;
import com.caresync.repository.MedicalDocumentRepository;
import com.caresync.dto.MedicalRecordResponse;
import com.caresync.entity.MedicalRecord;
import com.caresync.repository.MedicalRecordRepository;
import com.caresync.dto.CaregiverMedicationResponse;
import com.caresync.dto.CaregiverReminderResponse;
import com.caresync.entity.MedicationReminder;
import com.caresync.repository.MedicationReminderRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
@RestController
@RequestMapping("/api/caregiver/patients")
public class CaregiverAccessController {
    private final MedicationRepository medicationRepository;
private final AppointmentRepository appointmentRepository;
private final MedicalDocumentRepository medicalDocumentRepository;
private final MedicalRecordRepository medicalRecordRepository;
private final CaregiverRelationshipRepository relationshipRepository;
private final UserService userService;
private final MedicationReminderRepository medicationReminderRepository;
    
    public CaregiverAccessController(
        CaregiverRelationshipRepository relationshipRepository,
        UserService userService,
        MedicationRepository medicationRepository,
        AppointmentRepository appointmentRepository,
        MedicalDocumentRepository medicalDocumentRepository,
        MedicalRecordRepository medicalRecordRepository,
        MedicationReminderRepository medicationReminderRepository) {

    this.medicationRepository = medicationRepository;
    this.appointmentRepository = appointmentRepository;
    this.relationshipRepository = relationshipRepository;
    this.userService = userService;
    this.medicalDocumentRepository = medicalDocumentRepository;
    this.medicalRecordRepository = medicalRecordRepository;
    this.medicationReminderRepository = medicationReminderRepository;
}

    @GetMapping("/{patientId}/access")
    public ResponseEntity<?> checkAccess(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String caregiverEmail,

            @PathVariable("patientId")
            Long patientId) {

        if (caregiverEmail == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        User caregiver =
                userService.findByEmail(caregiverEmail);

        if (caregiver == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Caregiver not found");
        }

        CaregiverRelationship relationship =
                findRelationship(
                        caregiverEmail,
                        patientId
                );

        if (relationship == null ||
                !"ACCEPTED".equals(relationship.getStatus())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("No active caregiver access");
        }

        return ResponseEntity.ok(
                new AccessResponse(
                        relationship.getPatient().getId(),
                        relationship.isMedicationAccess(),
                        relationship.isAppointmentAccess(),
                        relationship.isHealthHistoryAccess(),
                        relationship.isReminderAccess(),
                        relationship.isDocumentAccess()
                )
        );
    }

    private CaregiverRelationship findRelationship(
            String caregiverEmail,
            Long patientId) {

        return relationshipRepository
                .findByCaregiverEmailAndPatientId(
                        caregiverEmail,
                        patientId
                );
    }

    public static class AccessResponse {

        private Long patientId;
        private boolean medicationAccess;
        private boolean appointmentAccess;
        private boolean healthHistoryAccess;
        private boolean reminderAccess;
        private boolean documentAccess;        
        public AccessResponse(
                Long patientId,
                boolean medicationAccess,
                boolean appointmentAccess,
                boolean healthHistoryAccess,
                boolean reminderAccess,
                boolean documentAccess) {

            this.patientId = patientId;
            this.medicationAccess = medicationAccess;
            this.appointmentAccess = appointmentAccess;
            this.healthHistoryAccess = healthHistoryAccess;
            this.reminderAccess = reminderAccess;
            this.documentAccess = documentAccess;
        }

        public Long getPatientId() {
            return patientId;
        }

        public boolean isMedicationAccess() {
            return medicationAccess;
        }

        public boolean isAppointmentAccess() {
            return appointmentAccess;
        }

        public boolean isHealthHistoryAccess() {
            return healthHistoryAccess;
        }

        public boolean isReminderAccess() {
            return reminderAccess;
        }

        public boolean isDocumentAccess() {
            return documentAccess;
        }
    }
    @GetMapping("/{patientId}/medications")
public ResponseEntity<?> getMedications(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String caregiverEmail,

        @PathVariable("patientId")
        Long patientId) {

    if (caregiverEmail == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    CaregiverRelationship relationship =
            findRelationship(
                    caregiverEmail,
                    patientId
            );

    if (relationship == null) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("No active caregiver access");
    }

    if (!relationship.isMedicationAccess()) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Medication access is not permitted");
    }

    User patient = relationship.getPatient();

    List<Medication> medications =
            medicationRepository.findByPatient(patient);

    List<CaregiverMedicationResponse> response =
            medications.stream()
                    .map(medication ->
                            new CaregiverMedicationResponse(
                                    medication.getId(),
                                    medication.getMedicineName(),
                                    medication.getDosage(),
                                    medication.getDosageUnit(),
                                    medication.getFrequency(),
                                    medication.getStartDate(),
                                    medication.getEndDate(),
                                    medication.getInstructions(),
                                    medication.getPrescribingDoctor(),
                                    medication.getPrescriptionReference()
                            )
                    )
                    .toList();

    return ResponseEntity.ok(response);
}
@GetMapping("/{patientId}/appointments")
public ResponseEntity<?> getAppointments(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String caregiverEmail,

        @PathVariable("patientId")
        Long patientId) {

    if (caregiverEmail == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    CaregiverRelationship relationship =
            findRelationship(
                    caregiverEmail,
                    patientId
            );

    if (relationship == null) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("No active caregiver access");
    }

    if (!relationship.isAppointmentAccess()) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Appointment access is not permitted");
    }

    User patient = relationship.getPatient();

    List<Appointment> appointments =
            appointmentRepository.findByPatient(patient);

    List<CaregiverAppointmentResponse> response =
            appointments.stream()
                    .map(appointment ->
                            new CaregiverAppointmentResponse(
                                    appointment.getId(),
                                    appointment.getDoctorName(),
                                    appointment.getHospitalOrClinic(),
                                    appointment.getAppointmentDate(),
                                    appointment.getAppointmentTime(),
                                    appointment.getReason(),
                                    appointment.getNotes(),
                                    appointment.getStatus(),
                                    appointment.getFollowUpDate(),
                                    appointment.getFollowUpInstructions()
                            )
                    )
                    .toList();

    return ResponseEntity.ok(response);
}
@GetMapping("/{patientId}/documents")
public ResponseEntity<?> getDocuments(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String caregiverEmail,

        @PathVariable("patientId")
        Long patientId) {

    if (caregiverEmail == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    CaregiverRelationship relationship =
            findRelationship(
                    caregiverEmail,
                    patientId
            );

    if (relationship == null) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("No active caregiver access");
    }

    if (!relationship.isDocumentAccess()) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Document access is not permitted");
    }

    User patient = relationship.getPatient();

    List<MedicalDocument> documents =
            medicalDocumentRepository.findByPatient(patient);

    List<CaregiverDocumentResponse> response =
            documents.stream()
                    .map(document ->
                            new CaregiverDocumentResponse(
                                    document.getId(),
                                    document.getFileName(),
                                    document.getFileType(),
                                    document.getFileSize(),
                                    document.getUploadedAt(),
                                    document.getDocumentCategory(),
                                    document.getAiClassified()
                            )
                    )
                    .toList();

    return ResponseEntity.ok(response);
}
@GetMapping("/{patientId}/health-history")
public ResponseEntity<?> getHealthHistory(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String caregiverEmail,

        @PathVariable("patientId")
        Long patientId) {

    if (caregiverEmail == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    CaregiverRelationship relationship =
            findRelationship(
                    caregiverEmail,
                    patientId
            );

    if (relationship == null) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("No active caregiver access");
    }

    if (!relationship.isHealthHistoryAccess()) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Health history access is not permitted");
    }

    User patient = relationship.getPatient();

    List<MedicalRecord> records =
            medicalRecordRepository.findByPatient(patient);

    List<MedicalRecordResponse> response =
            records.stream()
                    .map(record -> {

                        MedicalRecordResponse dto =
                                new MedicalRecordResponse();

                        dto.setId(record.getId());

                        dto.setPatientName(
                                patient.getName()
                        );

                        dto.setPatientEmail(
                                patient.getEmail()
                        );

                        dto.setDoctorName(
                                record.getDoctorName()
                        );

                        dto.setRecordDate(
                                record.getRecordDate()
                        );

                        dto.setDiagnosis(
                                record.getDiagnosis()
                        );

                        dto.setDescription(
                                record.getDescription()
                        );

                        dto.setTreatment(
                                record.getTreatment()
                        );

                        return dto;
                    })
                    .toList();

    return ResponseEntity.ok(response);
}
@GetMapping("/{patientId}/reminders")
public ResponseEntity<?> getReminders(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String caregiverEmail,

        @PathVariable("patientId")
        Long patientId) {

    if (caregiverEmail == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    CaregiverRelationship relationship =
            findRelationship(
                    caregiverEmail,
                    patientId
            );

    if (relationship == null) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("No active caregiver access");
    }

    if (!relationship.isReminderAccess()) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Reminder access is not permitted");
    }

    User patient = relationship.getPatient();

    List<MedicationReminder> reminders =
            medicationReminderRepository
                    .findByPatient(patient);

    List<CaregiverReminderResponse> response =
            reminders.stream()
                    .map(reminder ->
                            new CaregiverReminderResponse(
                                    reminder.getId(),
                                    reminder.getMedication().getId(),
                                    reminder.getMedication().getMedicineName(),
                                    reminder.getReminderDate(),
                                    reminder.getReminderTime(),
                                    reminder.getStatus(),
                                    reminder.getSnoozedUntil()
                            )
                    )
                    .toList();

    return ResponseEntity.ok(response);
}
@GetMapping("/{patientId}/documents/{documentId}/download")
public ResponseEntity<?> downloadDocument(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String caregiverEmail,

        @PathVariable("patientId")
        Long patientId,

        @PathVariable("documentId")
        Long documentId) {

    if (caregiverEmail == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    CaregiverRelationship relationship =
            findRelationship(
                    caregiverEmail,
                    patientId
            );

    if (relationship == null) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("No active caregiver access");
    }

    if (!relationship.isDocumentAccess()) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Document access is not permitted");
    }

    MedicalDocument document =
            medicalDocumentRepository.findById(documentId);

    if (document == null) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Document not found");
    }

    if (!document.getPatient().getId().equals(patientId)) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Document does not belong to this patient");
    }

    Resource resource =
            new FileSystemResource(
                    document.getFilePath()
            );

    if (!resource.exists()) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("File not found");
    }

    MediaType mediaType;

    try {
        mediaType =
                MediaType.parseMediaType(
                        document.getFileType()
                );
    } catch (Exception e) {
        mediaType =
                MediaType.APPLICATION_OCTET_STREAM;
    }

    return ResponseEntity.ok()
            .contentType(mediaType)
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" +
                            document.getFileName() +
                            "\""
            )
            .body(resource);
}
}