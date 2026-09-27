package com.caresync.controller;

import com.caresync.dto.RefillRequestResponse;
import com.caresync.entity.RefillRequest;
import com.caresync.service.RefillRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/refills")
public class RefillRequestController {

    private final RefillRequestService refillService;

    public RefillRequestController(
            RefillRequestService refillService) {

        this.refillService = refillService;
    }

    @PostMapping
    public ResponseEntity<?> createRefill(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @RequestParam("medicationId")
            Long medicationId,

            @RequestParam("pharmacyId")
            Long pharmacyId,

            @RequestParam("requestedQuantity")
            Integer requestedQuantity,

            @RequestParam(
                    value = "prescriptionReference",
                    required = false)
            String prescriptionReference) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            RefillRequest request =
                    refillService.createRefillRequest(
                            email,
                            medicationId,
                            pharmacyId,
                            requestedQuantity,
                            prescriptionReference
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(request));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyRefills(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            List<RefillRequestResponse> requests =
                    refillService
                            .getMyRefillRequests(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(requests);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("id")
            Long id,

            @RequestParam("status")
            String status) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            RefillRequest request =
                    refillService.updateStatus(
                            email,
                            id,
                            status.toUpperCase()
                    );

            return ResponseEntity.ok(
                    toResponse(request)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private RefillRequestResponse toResponse(
            RefillRequest request) {

        RefillRequestResponse response =
                new RefillRequestResponse();

        response.setId(request.getId());

        response.setMedicationId(
                request.getMedication().getId()
        );

        response.setMedicineName(
                request.getMedication().getMedicineName()
        );

        response.setPharmacyId(
                request.getPharmacy().getId()
        );

        response.setPharmacyName(
                request.getPharmacy().getName()
        );

        response.setRequestedQuantity(
                request.getRequestedQuantity()
        );

        response.setPrescriptionReference(
                request.getPrescriptionReference()
        );

        response.setStatus(
                request.getStatus()
        );

        response.setCreatedAt(
                request.getCreatedAt()
        );

        return response;
    }
}