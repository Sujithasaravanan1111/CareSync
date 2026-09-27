package com.caresync.controller;

import com.caresync.dto.PatientProfileResponse;
import com.caresync.entity.PatientProfile;
import com.caresync.entity.User;
import com.caresync.service.PatientProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient/profile")
public class PatientProfileController {

    private final PatientProfileService profileService;

    public PatientProfileController(
            PatientProfileService profileService) {

        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<?> createProfile(
            @RequestAttribute("authenticatedUser")
            String email,
            @RequestBody PatientProfile profile) {

        try {

            PatientProfile saved =
                    profileService.createOrUpdateProfile(
                            email,
                            profile
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(saved));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getProfile(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        PatientProfile profile =
                profileService.getProfile(email);

        if (profile == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Patient profile not found");
        }

        return ResponseEntity.ok(toResponse(profile));
    }

    private PatientProfileResponse toResponse(
            PatientProfile profile) {

        PatientProfileResponse response =
                new PatientProfileResponse();

        User user = profile.getUser();

        response.setId(profile.getId());

        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        response.setDateOfBirth(
                profile.getDateOfBirth());

        response.setGender(
                profile.getGender());

        response.setBloodGroup(
                profile.getBloodGroup());

        response.setPhone(
                profile.getPhone());

        response.setAddress(
                profile.getAddress());

        response.setEmergencyContactName(
                profile.getEmergencyContactName());

        response.setEmergencyContactPhone(
                profile.getEmergencyContactPhone());

        response.setEmergencyContactRelationship(
                profile.getEmergencyContactRelationship());

        response.setAllergies(
                profile.getAllergies());

        response.setChronicConditions(
                profile.getChronicConditions());

        response.setMedicalHistory(
                profile.getMedicalHistory());

        return response;
    }
    @PutMapping
public ResponseEntity<?> updateProfile(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        @RequestBody PatientProfile profile) {

    if (email == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    try {

        PatientProfile updated =
                profileService.createOrUpdateProfile(
                        email,
                        profile
                );

        return ResponseEntity.ok(
                toResponse(updated)
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
}