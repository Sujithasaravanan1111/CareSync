package com.caresync.service;

import com.caresync.entity.EmergencyHealthCapsule;
import com.caresync.entity.User;
import com.caresync.repository.EmergencyHealthCapsuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmergencyHealthCapsuleService {

    private final EmergencyHealthCapsuleRepository repository;
    private final UserService userService;

    public EmergencyHealthCapsuleService(
            EmergencyHealthCapsuleRepository repository,
            UserService userService) {

        this.repository = repository;
        this.userService = userService;
    }

    @Transactional
    public EmergencyHealthCapsule createOrUpdate(
            String email,
            String bloodGroup,
            String criticalAllergies,
            String importantConditions,
            String currentMedicines,
            String emergencyContactName,
            String emergencyContactPhone,
            String importantMedicalInformation) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("Patient not found");
        }

        EmergencyHealthCapsule capsule =
                repository.findByPatient(patient);

        if (capsule == null) {
            capsule = new EmergencyHealthCapsule();
            capsule.setPatient(patient);
        }

        capsule.setBloodGroup(bloodGroup);
        capsule.setCriticalAllergies(criticalAllergies);
        capsule.setImportantConditions(
                importantConditions
        );
        capsule.setCurrentMedicines(
                currentMedicines
        );
        capsule.setEmergencyContactName(
                emergencyContactName
        );
        capsule.setEmergencyContactPhone(
                emergencyContactPhone
        );
        capsule.setImportantMedicalInformation(
                importantMedicalInformation
        );

        capsule.setActive(true);
        capsule.setUpdatedAt(LocalDateTime.now());

        if (capsule.getId() == null) {
            return repository.save(capsule);
        }

        return repository.update(capsule);
    }

    @Transactional(readOnly = true)
public EmergencyHealthCapsule getMyCapsule(
        String email) {

    User patient = userService.findByEmail(email);

    if (patient == null) {
        throw new RuntimeException("Patient not found");
    }

    EmergencyHealthCapsule capsule =
            repository.findByPatient(patient);

    if (capsule == null) {
        throw new RuntimeException(
                "Emergency capsule not found"
        );
    }

    if (!capsule.isActive()) {
        throw new RuntimeException(
                "Emergency capsule is currently inactive"
        );
    }

    return capsule;
}

    @Transactional
public EmergencyHealthCapsule setActive(
        String email,
        boolean active) {

    User patient = userService.findByEmail(email);

    if (patient == null) {
        throw new RuntimeException("Patient not found");
    }

    EmergencyHealthCapsule capsule =
            repository.findByPatient(patient);

    if (capsule == null) {
        throw new RuntimeException(
                "Emergency capsule not found"
        );
    }

    capsule.setActive(active);
    capsule.setUpdatedAt(LocalDateTime.now());

    return repository.update(capsule);
}
}