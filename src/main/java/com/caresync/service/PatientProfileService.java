package com.caresync.service;

import com.caresync.entity.PatientProfile;
import com.caresync.entity.User;
import com.caresync.repository.PatientProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientProfileService {

    private final PatientProfileRepository profileRepository;
    private final UserService userService;

    public PatientProfileService(
            PatientProfileRepository profileRepository,
            UserService userService) {

        this.profileRepository = profileRepository;
        this.userService = userService;
    }

    @Transactional
    public PatientProfile createOrUpdateProfile(
            String email,
            PatientProfile profileData) {

        User user = userService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        PatientProfile existing =
                profileRepository.findByUser(user);

        if (existing == null) {

            profileData.setUser(user);

            return profileRepository.save(profileData);
        }

        existing.setDateOfBirth(profileData.getDateOfBirth());
        existing.setGender(profileData.getGender());
        existing.setBloodGroup(profileData.getBloodGroup());
        existing.setPhone(profileData.getPhone());
        existing.setAddress(profileData.getAddress());
        existing.setEmergencyContactName(
                profileData.getEmergencyContactName());
        existing.setEmergencyContactPhone(
                profileData.getEmergencyContactPhone());
        existing.setEmergencyContactRelationship(
                profileData.getEmergencyContactRelationship());
        existing.setAllergies(profileData.getAllergies());
        existing.setChronicConditions(
                profileData.getChronicConditions());
        existing.setMedicalHistory(
                profileData.getMedicalHistory());

        return profileRepository.update(existing);
    }
    @Transactional(readOnly = true)
    public PatientProfile getProfile(String email) {

        User user = userService.findByEmail(email);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return profileRepository.findByUser(user);
    }
}