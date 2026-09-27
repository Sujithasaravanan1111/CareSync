package com.caresync.service;

import com.caresync.entity.Medication;
import com.caresync.entity.Pharmacy;
import com.caresync.entity.RefillRequest;
import com.caresync.entity.User;
import com.caresync.repository.MedicationRepository;
import com.caresync.repository.PharmacyRepository;
import com.caresync.repository.RefillRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RefillRequestService {

    private final RefillRequestRepository refillRepository;
    private final MedicationRepository medicationRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PharmacyIntegrationService pharmacyIntegrationService;
    private final UserService userService;

    public RefillRequestService(
            RefillRequestRepository refillRepository,
            MedicationRepository medicationRepository,
            PharmacyRepository pharmacyRepository,
            PharmacyIntegrationService pharmacyIntegrationService,
            UserService userService) {

        this.refillRepository = refillRepository;
        this.medicationRepository = medicationRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyIntegrationService = pharmacyIntegrationService;
        this.userService = userService;
    }

    @Transactional
    public RefillRequest createRefillRequest(
            String email,
            Long medicationId,
            Long pharmacyId,
            Integer requestedQuantity,
            String prescriptionReference) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        if (requestedQuantity == null ||
                requestedQuantity <= 0) {

            throw new RuntimeException(
                    "Requested quantity must be greater than zero"
            );
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

        Pharmacy pharmacy =
                pharmacyRepository.findById(pharmacyId);

        if (pharmacy == null) {
            throw new RuntimeException(
                    "Pharmacy not found"
            );
        }

        if (!pharmacy.isAvailable()) {
            throw new RuntimeException(
                    "Selected pharmacy is currently unavailable"
            );
        }

        PharmacyIntegrationService.PharmacyAvailabilityResult
                availability =
                pharmacyIntegrationService.checkAvailability(
                        pharmacyId,
                        medication.getMedicineName(),
                        requestedQuantity
                );

        RefillRequest request =
                new RefillRequest();

        request.setPatient(patient);
        request.setMedication(medication);
        request.setPharmacy(pharmacy);
        request.setRequestedQuantity(
                requestedQuantity
        );
        request.setPrescriptionReference(
                prescriptionReference
        );

        if (availability.isAvailable()) {

            request.setStatus("AVAILABLE");

        } else {

            request.setStatus("UNAVAILABLE");
        }

        return refillRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<RefillRequest> getMyRefillRequests(
            String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        return refillRepository.findByPatient(patient);
    }

    @Transactional
    public RefillRequest updateStatus(
            String email,
            Long refillId,
            String status) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        RefillRequest request =
                refillRepository.findById(refillId);

        if (request == null ||
                !request.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Refill request not found"
            );
        }

        if (!status.equals("PROCESSING") &&
                !status.equals("COMPLETED") &&
                !status.equals("CANCELLED")) {

            throw new RuntimeException(
                    "Invalid refill status"
            );
        }

        if (status.equals("PROCESSING") &&
                !request.getStatus()
                        .equals("AVAILABLE")) {

            throw new RuntimeException(
                    "Only available refills can be processed"
            );
        }

        if (status.equals("COMPLETED") &&
                !request.getStatus()
                        .equals("PROCESSING")) {

            throw new RuntimeException(
                    "Only processing refills can be completed"
            );
        }

        if (status.equals("CANCELLED") &&
                request.getStatus()
                        .equals("COMPLETED")) {

            throw new RuntimeException(
                    "Completed refill cannot be cancelled"
            );
        }

        request.setStatus(status);

        return refillRepository.update(request);
    }
}