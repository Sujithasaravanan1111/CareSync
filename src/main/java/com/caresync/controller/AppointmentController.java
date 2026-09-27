package com.caresync.controller;

import com.caresync.dto.AppointmentResponse;
import com.caresync.entity.Appointment;
import com.caresync.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @RequestParam("doctorName")
            String doctorName,

            @RequestParam("hospitalOrClinic")
            String hospitalOrClinic,

            @RequestParam("appointmentDate")
            String appointmentDate,

            @RequestParam("appointmentTime")
            String appointmentTime,

            @RequestParam(
                    value = "reason",
                    required = false)
            String reason,

            @RequestParam(
                    value = "notes",
                    required = false)
            String notes,

            @RequestParam(
                    value = "followUpDate",
                    required = false)
            String followUpDate,

            @RequestParam(
                    value = "followUpInstructions",
                    required = false)
            String followUpInstructions) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            Appointment appointment =
                    appointmentService.createAppointment(
                            email,
                            doctorName,
                            hospitalOrClinic,
                            LocalDate.parse(appointmentDate),
                            LocalTime.parse(appointmentTime),
                            reason,
                            notes,
                            parseDate(followUpDate),
                            followUpInstructions
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(appointment));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAppointments(
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

            List<AppointmentResponse> appointments =
                    appointmentService
                            .getAppointments(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(appointments);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointment(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("id")
            Long id) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            return ResponseEntity.ok(
                    toResponse(
                            appointmentService
                                    .getAppointment(email, id)
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("id")
            Long id,

            @RequestParam("doctorName")
            String doctorName,

            @RequestParam("hospitalOrClinic")
            String hospitalOrClinic,

            @RequestParam("appointmentDate")
            String appointmentDate,

            @RequestParam("appointmentTime")
            String appointmentTime,

            @RequestParam(
                    value = "reason",
                    required = false)
            String reason,

            @RequestParam(
                    value = "notes",
                    required = false)
            String notes,

            @RequestParam(
                    value = "followUpDate",
                    required = false)
            String followUpDate,

            @RequestParam(
                    value = "followUpInstructions",
                    required = false)
            String followUpInstructions) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            Appointment appointment =
                    appointmentService.updateAppointment(
                            email,
                            id,
                            doctorName,
                            hospitalOrClinic,
                            LocalDate.parse(appointmentDate),
                            LocalTime.parse(appointmentTime),
                            reason,
                            notes,
                            parseDate(followUpDate),
                            followUpInstructions
                    );

            return ResponseEntity.ok(
                    toResponse(appointment)
            );

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

            Appointment appointment =
                    appointmentService.updateStatus(
                            email,
                            id,
                            status.toUpperCase()
                    );

            return ResponseEntity.ok(
                    toResponse(appointment)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("id")
            Long id) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            appointmentService.deleteAppointment(
                    email,
                    id
            );

            return ResponseEntity.ok(
                    "Appointment deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private LocalDate parseDate(String date) {

        if (date == null ||
                date.trim().isEmpty()) {

            return null;
        }

        return LocalDate.parse(date);
    }

    private AppointmentResponse toResponse(
            Appointment appointment) {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(appointment.getId());
        response.setDoctorName(
                appointment.getDoctorName()
        );
        response.setHospitalOrClinic(
                appointment.getHospitalOrClinic()
        );
        response.setAppointmentDate(
                appointment.getAppointmentDate()
        );
        response.setAppointmentTime(
                appointment.getAppointmentTime()
        );
        response.setReason(
                appointment.getReason()
        );
        response.setNotes(
                appointment.getNotes()
        );
        response.setStatus(
                appointment.getStatus()
        );
        response.setFollowUpDate(
                appointment.getFollowUpDate()
        );
        response.setFollowUpInstructions(
                appointment.getFollowUpInstructions()
        );

        return response;
    }
}