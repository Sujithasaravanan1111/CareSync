package com.caresync.controller;

import com.caresync.dto.HealthcareInstructionResponse;
import com.caresync.entity.HealthcareInstruction;
import com.caresync.service.HealthcareInstructionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/instructions")
public class HealthcareInstructionController {

    private final HealthcareInstructionService service;

    public HealthcareInstructionController(
            HealthcareInstructionService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createInstruction(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @RequestParam(
                    value = "medicationId",
                    required = false)
            Long medicationId,

            @RequestParam(
                    value = "appointmentId",
                    required = false)
            Long appointmentId,

            @RequestParam(
                    value = "doctorName",
                    required = false)
            String doctorName,

            @RequestParam("instruction")
            String instruction) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            HealthcareInstruction result =
                    service.createInstruction(
                            email,
                            medicationId,
                            appointmentId,
                            doctorName,
                            instruction
                    );

            boolean conflict = false;

            if (medicationId != null) {

                List<HealthcareInstruction> history =
                        service.getMedicationHistory(
                                email,
                                medicationId
                        );

                if (history.size() >= 2) {

                    HealthcareInstruction previous =
                            history.get(history.size() - 2);

                    conflict =
                            service.detectConflict(
                                    previous.getInstruction(),
                                    result.getInstruction()
                            );
                }
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(result, conflict));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getInstructions(
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

            List<HealthcareInstructionResponse> result =
                    service.getMyInstructions(email)
                            .stream()
                            .map(i -> toResponse(i, false))
                            .collect(Collectors.toList());

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/medication/{medicationId}")
    public ResponseEntity<?> getMedicationHistory(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("medicationId")
            Long medicationId) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            List<HealthcareInstruction> history =
                    service.getMedicationHistory(
                            email,
                            medicationId
                    );

            return ResponseEntity.ok(
                    history.stream()
                            .map(i -> toResponse(i, false))
                            .collect(Collectors.toList())
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
public ResponseEntity<?> updateInstruction(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,

        @PathVariable("id")
        Long id,

        @RequestParam(
                value = "instruction",
                required = false)
        String instruction,

        @RequestParam(
                value = "instructionType",
                required = false)
        String instructionType,

        @RequestParam(
                value = "startDate",
                required = false)
        String startDate,

        @RequestParam(
                value = "endDate",
                required = false)
        String endDate,

        @RequestParam(
                value = "status",
                required = false)
        String status) {

    if (email == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    try {

        HealthcareInstruction updated =
                service.updateInstruction(
                        email,
                        id,
                        instruction,
                        instructionType,
                        startDate,
                        endDate,
                        status
                );

        return ResponseEntity.ok(
                toResponse(updated, false)
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}

    private HealthcareInstructionResponse toResponse(
            HealthcareInstruction instruction,
            boolean conflict) {

        HealthcareInstructionResponse response =
                new HealthcareInstructionResponse();

        response.setId(instruction.getId());

        if (instruction.getMedication() != null) {
            response.setMedicationId(
                    instruction.getMedication().getId()
            );
        }

        if (instruction.getAppointment() != null) {
            response.setAppointmentId(
                    instruction.getAppointment().getId()
            );
        }

        response.setDoctorName(
                instruction.getDoctorName()
        );

        response.setInstruction(
                instruction.getInstruction()
        );

        response.setVersion(
                instruction.getVersion()
        );

        response.setCreatedAt(
                instruction.getCreatedAt()
        );
        response.setInstructionType(
        instruction.getInstructionType()
);

response.setInstructionDate(
        instruction.getInstructionDate()
);

response.setStartDate(
        instruction.getStartDate()
);

response.setEndDate(
        instruction.getEndDate()
);

response.setSource(
        instruction.getSource()
);

response.setVerificationStatus(
        instruction.getVerificationStatus()
);

response.setStatus(
        instruction.getStatus()
);

        response.setPotentialConflict(conflict);

        return response;
    }
}