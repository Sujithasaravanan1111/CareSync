package com.caresync.service;

import com.caresync.entity.Appointment;
import com.caresync.entity.User;
import com.caresync.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserService userService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserService userService) {

        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
    }

    @Transactional
    public Appointment createAppointment(
            String email,
            String doctorName,
            String hospitalOrClinic,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String reason,
            String notes,
            LocalDate followUpDate,
            String followUpInstructions) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        validateAppointment(
                doctorName,
                hospitalOrClinic,
                appointmentDate,
                appointmentTime
        );

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctorName(doctorName);
        appointment.setHospitalOrClinic(hospitalOrClinic);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setReason(reason);
        appointment.setNotes(notes);
        appointment.setStatus("SCHEDULED");
        appointment.setFollowUpDate(followUpDate);
        appointment.setFollowUpInstructions(
                followUpInstructions
        );

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(
            String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        return appointmentRepository.findByPatient(patient);
    }

    @Transactional(readOnly = true)
    public Appointment getAppointment(
            String email,
            Long id) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        Appointment appointment =
                appointmentRepository.findById(id);

        validateOwnership(appointment, patient);

        return appointment;
    }

    @Transactional
    public Appointment updateAppointment(
            String email,
            Long id,
            String doctorName,
            String hospitalOrClinic,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String reason,
            String notes,
            LocalDate followUpDate,
            String followUpInstructions) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        Appointment appointment =
                appointmentRepository.findById(id);

        validateOwnership(appointment, patient);

        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException(
                    "Cancelled appointment cannot be edited"
            );
        }

        validateAppointment(
                doctorName,
                hospitalOrClinic,
                appointmentDate,
                appointmentTime
        );

        appointment.setDoctorName(doctorName);
        appointment.setHospitalOrClinic(hospitalOrClinic);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setReason(reason);
        appointment.setNotes(notes);
        appointment.setFollowUpDate(followUpDate);
        appointment.setFollowUpInstructions(
                followUpInstructions
        );

        return appointmentRepository.update(appointment);
    }

    @Transactional
    public Appointment updateStatus(
            String email,
            Long id,
            String status) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        Appointment appointment =
                appointmentRepository.findById(id);

        validateOwnership(appointment, patient);

        if (!status.equals("SCHEDULED") &&
                !status.equals("COMPLETED") &&
                !status.equals("CANCELLED") &&
                !status.equals("RESCHEDULED")) {

            throw new RuntimeException(
                    "Invalid appointment status"
            );
        }

        if ("COMPLETED".equals(appointment.getStatus()) &&
                !"COMPLETED".equals(status)) {

            throw new RuntimeException(
                    "Completed appointment cannot change status"
            );
        }

        if ("CANCELLED".equals(appointment.getStatus()) &&
                !"CANCELLED".equals(status)) {

            throw new RuntimeException(
                    "Cancelled appointment cannot change status"
            );
        }

        appointment.setStatus(status);

        return appointmentRepository.update(appointment);
    }

    @Transactional
    public void deleteAppointment(
            String email,
            Long id) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        Appointment appointment =
                appointmentRepository.findById(id);

        validateOwnership(appointment, patient);

        appointmentRepository.delete(appointment);
    }

    private void validateOwnership(
            Appointment appointment,
            User patient) {

        if (appointment == null ||
                !appointment.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Appointment not found"
            );
        }
    }

    private void validateAppointment(
            String doctorName,
            String hospitalOrClinic,
            LocalDate date,
            LocalTime time) {

        if (doctorName == null ||
                doctorName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Doctor name is required"
            );
        }

        if (hospitalOrClinic == null ||
                hospitalOrClinic.trim().isEmpty()) {

            throw new RuntimeException(
                    "Hospital or clinic is required"
            );
        }

        if (date == null) {
            throw new RuntimeException(
                    "Appointment date is required"
            );
        }

        if (time == null) {
            throw new RuntimeException(
                    "Appointment time is required"
            );
        }
    }
}