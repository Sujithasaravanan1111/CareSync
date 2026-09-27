package com.caresync.service;

import com.caresync.entity.CaregiverRelationship;
import com.caresync.entity.User;
import com.caresync.repository.CaregiverRelationshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CaregiverService {

    private final CaregiverRelationshipRepository repository;
    private final UserService userService;

    public CaregiverService(
            CaregiverRelationshipRepository repository,
            UserService userService) {

        this.repository = repository;
        this.userService = userService;
    }

    @Transactional
public CaregiverRelationship inviteCaregiver(
        String patientEmail,
        String caregiverEmail) {

    User patient =
            userService.findByEmail(patientEmail);

    if (patient == null) {
        throw new RuntimeException("Patient not found");
    }

    if (caregiverEmail == null ||
            caregiverEmail.trim().isEmpty()) {

        throw new RuntimeException(
                "Caregiver email is required"
        );
    }

    String normalizedCaregiverEmail =
            caregiverEmail.trim().toLowerCase();

    if (patientEmail.equalsIgnoreCase(
            normalizedCaregiverEmail)) {

        throw new RuntimeException(
                "Patient cannot add themselves as caregiver"
        );
    }

    CaregiverRelationship existing =
            repository.findByPatientAndEmail(
                    patient,
                    normalizedCaregiverEmail
            );

    /*
     * If an active/pending relationship already exists,
     * do not create another one.
     */
    if (existing != null &&
            !"REVOKED".equals(existing.getStatus())) {

        throw new RuntimeException(
                "Caregiver invitation already exists"
        );
    }

    /*
     * If the previous relationship was revoked,
     * reuse the same database row instead of creating
     * a duplicate relationship.
     */
    CaregiverRelationship relationship;

    if (existing != null &&
            "REVOKED".equals(existing.getStatus())) {

        relationship = existing;

        relationship.setStatus("PENDING");

        relationship.setMedicationAccess(false);
        relationship.setAppointmentAccess(false);
        relationship.setHealthHistoryAccess(false);
        relationship.setReminderAccess(false);
        relationship.setDocumentAccess(false);

    } else {

        relationship =
                new CaregiverRelationship();

        relationship.setPatient(patient);

        relationship.setCaregiverEmail(
                normalizedCaregiverEmail
        );

        relationship.setStatus("PENDING");

        relationship.setMedicationAccess(false);
        relationship.setAppointmentAccess(false);
        relationship.setHealthHistoryAccess(false);
        relationship.setReminderAccess(false);
        relationship.setDocumentAccess(false);
    }

    return repository.save(relationship);
}
    @Transactional
    public CaregiverRelationship acceptInvitation(
            String caregiverEmail,
            Long relationshipId) {

        User caregiver =
                userService.findByEmail(caregiverEmail);

        if (caregiver == null) {
            throw new RuntimeException(
                    "Caregiver user not found"
            );
        }

        CaregiverRelationship relationship =
                repository.findById(relationshipId);

        if (relationship == null) {
            throw new RuntimeException(
                    "Invitation not found"
            );
        }

        if (!relationship.getCaregiverEmail()
                .equalsIgnoreCase(caregiverEmail)) {

            throw new RuntimeException(
                    "This invitation belongs to another email"
            );
        }

        if (!"PENDING".equals(relationship.getStatus())) {
            throw new RuntimeException(
                    "Invitation is not pending"
            );
        }

        relationship.setCaregiver(caregiver);
        relationship.setStatus("ACCEPTED");

        return repository.update(relationship);
    }

    @Transactional(readOnly = true)
    public List<CaregiverRelationship> getMyCaregivers(
            String patientEmail) {

        User patient =
                userService.findByEmail(patientEmail);

        if (patient == null) {
            throw new RuntimeException(
                    "Patient not found"
            );
        }

        return repository.findByPatient(patient);
    }

    @Transactional(readOnly = true)
    public List<CaregiverRelationship> getSharedPatients(
            String caregiverEmail) {

        User caregiver =
                userService.findByEmail(caregiverEmail);

        if (caregiver == null) {
            throw new RuntimeException(
                    "Caregiver not found"
            );
        }

        return repository.findByCaregiver(caregiver);
    }

    @Transactional
    public CaregiverRelationship updatePermissions(
            String patientEmail,
            Long relationshipId,
            Boolean medicationAccess,
            Boolean appointmentAccess,
            Boolean healthHistoryAccess,
            Boolean reminderAccess,
            Boolean documentAccess) {

        User patient =
                userService.findByEmail(patientEmail);

        if (patient == null) {
            throw new RuntimeException(
                    "Patient not found"
            );
        }

        CaregiverRelationship relationship =
                repository.findById(relationshipId);

        validatePatientOwnership(
                relationship,
                patient
        );

        if (!"ACCEPTED".equals(relationship.getStatus())) {
            throw new RuntimeException(
                    "Caregiver has not accepted the invitation"
            );
        }

        if (medicationAccess != null) {
    relationship.setMedicationAccess(medicationAccess);
}

if (appointmentAccess != null) {
    relationship.setAppointmentAccess(appointmentAccess);
}

if (healthHistoryAccess != null) {
    relationship.setHealthHistoryAccess(healthHistoryAccess);
}

if (reminderAccess != null) {
    relationship.setReminderAccess(reminderAccess);
}

if (documentAccess != null) {
    relationship.setDocumentAccess(documentAccess);
}

        return repository.update(relationship);
    }

    @Transactional
    public void revokeCaregiver(
            String patientEmail,
            Long relationshipId) {

        User patient =
                userService.findByEmail(patientEmail);

        if (patient == null) {
            throw new RuntimeException(
                    "Patient not found"
            );
        }

        CaregiverRelationship relationship =
                repository.findById(relationshipId);

        validatePatientOwnership(
                relationship,
                patient
        );

        relationship.setStatus("REVOKED");

        relationship.setMedicationAccess(false);
        relationship.setAppointmentAccess(false);
        relationship.setHealthHistoryAccess(false);
        relationship.setReminderAccess(false);
        relationship.setDocumentAccess(false);

        repository.update(relationship);
    }

    private void validatePatientOwnership(
            CaregiverRelationship relationship,
            User patient) {

        if (relationship == null ||
                !relationship.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Caregiver relationship not found"
            );
        }
    }
}