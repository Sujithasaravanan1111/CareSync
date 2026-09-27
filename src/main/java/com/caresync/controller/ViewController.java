package com.caresync.controller;
import com.caresync.entity.Medication;
import com.caresync.entity.User;
import com.caresync.repository.AppointmentRepository;
import com.caresync.repository.MedicalDocumentRepository;
import com.caresync.repository.MedicationReminderRepository;
import com.caresync.repository.MedicationRepository;
import com.caresync.service.UserService;
import com.caresync.repository.MedicalRecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

@Controller
public class ViewController {

    private final UserService userService;
    private final MedicationRepository medicationRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalDocumentRepository medicalDocumentRepository;
    private final MedicationReminderRepository medicationReminderRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public ViewController(
            UserService userService,
            MedicationRepository medicationRepository,
            AppointmentRepository appointmentRepository,
            MedicalDocumentRepository medicalDocumentRepository,
            MedicationReminderRepository medicationReminderRepository,MedicalRecordRepository medicalRecordRepository) {

        this.userService = userService;
        this.medicationRepository = medicationRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalDocumentRepository = medicalDocumentRepository;
        this.medicationReminderRepository = medicationReminderRepository;
        this.medicalRecordRepository =  medicalRecordRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/patient/dashboard")
    public String patientDashboard(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            Model model) {

        if (email == null) {
            return "redirect:/login";
        }

        User patient = userService.findByEmail(email);

        if (patient == null) {
            return "redirect:/login";
        }

        /*
         * Patient information
         */
        model.addAttribute(
                "patientName",
                patient.getName()
        );

        model.addAttribute(
                "patientEmail",
                patient.getEmail()
        );

        /*
         * Dashboard counts
         */
        model.addAttribute(
                "medicationCount",
                medicationRepository
                        .findByPatient(patient)
                        .size()
        );

        model.addAttribute(
                "appointmentCount",
                appointmentRepository
                        .findByPatient(patient)
                        .size()
        );

        model.addAttribute(
                "documentCount",
                medicalDocumentRepository
                        .findByPatient(patient)
                        .size()
        );

        model.addAttribute(
                "reminderCount",
                medicationReminderRepository
                        .findByPatient(patient)
                        .size()
        );

        /*
         * Dashboard lists
         */
        model.addAttribute(
                "appointments",
                appointmentRepository
                        .findByPatient(patient)
        );

        model.addAttribute(
                "reminders",
                medicationReminderRepository
                        .findByPatient(patient)
        );

        model.addAttribute(
                "documents",
                medicalDocumentRepository
                        .findByPatient(patient)
        );

        /*
         * Notification count
         * Keep zero until notification module is connected.
         */
        model.addAttribute(
                "notificationCount",
                0
        );

        return "patient-dashboard";
    }
    @GetMapping("/patient/medications")
public String medicationsPage(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        Model model) {

    if (email == null) {
        return "redirect:/login";
    }

    User patient = userService.findByEmail(email);

    if (patient == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "patientName",
            patient.getName()
    );

    model.addAttribute(
            "patientEmail",
            patient.getEmail()
    );

    model.addAttribute(
            "medications",
            medicationRepository.findByPatient(patient)
    );

    return "medications";
}

@GetMapping("/patient/appointments")
public String appointmentsPage(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        Model model) {

    if (email == null) {
        return "redirect:/login";
    }

    User patient = userService.findByEmail(email);

    if (patient == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "patientName",
            patient.getName()
    );

    model.addAttribute(
            "patientEmail",
            patient.getEmail()
    );

    model.addAttribute(
            "appointments",
            appointmentRepository.findByPatient(patient)
    );

    return "appointments";
}
@GetMapping("/patient/documents")
public String documentsPage(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        Model model) {

    if (email == null) {
        return "redirect:/login";
    }

    User patient = userService.findByEmail(email);

    if (patient == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "patientName",
            patient.getName()
    );

    model.addAttribute(
            "patientEmail",
            patient.getEmail()
    );

    model.addAttribute(
            "documents",
            medicalDocumentRepository.findByPatient(patient)
    );

    return "documents";
}
@GetMapping("/patient/reminders")
public String remindersPage(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        Model model) {

    if (email == null) {
        return "redirect:/login";
    }

    User patient = userService.findByEmail(email);

    if (patient == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "patientName",
            patient.getName()
    );

    model.addAttribute(
            "patientEmail",
            patient.getEmail()
    );

    model.addAttribute(
            "reminders",
            medicationReminderRepository.findByPatient(patient)
    );

    model.addAttribute(
            "medications",
            medicationRepository.findByPatient(patient)
    );

    return "reminders";
}
@GetMapping("/patient/health-records")
public String healthRecordsPage(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        Model model) {

    if (email == null) {
        return "redirect:/login";
    }

    User patient = userService.findByEmail(email);

    if (patient == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "patientName",
            patient.getName()
    );

    model.addAttribute(
            "patientEmail",
            patient.getEmail()
    );

    model.addAttribute(
            "records",
            medicalRecordRepository.findByPatient(patient)
    );

    return "health-records";
}
@GetMapping("/patient/profile")
public String patientProfile(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        Model model) {

    if (email == null) {
        return "redirect:/login";
    }

    User patient = userService.findByEmail(email);

    if (patient == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "patientName",
            patient.getName()
    );

    model.addAttribute(
            "patientEmail",
            patient.getEmail()
    );

    return "profile";
}
// =========================================================
// CAREGIVER ACCESS
// =========================================================

@GetMapping("/patient/caregivers")
public String caregiverAccess() {
    return "caregiver-access";
}
@GetMapping("/caregiver/patient-details")
public String caregiverPatientDetails() {
    return "patient-details";
}
@GetMapping("/caregiver/dashboard")
    public String caregiverDashboard() {
        return "caregiver-dashboard";
    }
    @GetMapping("/patient/health-timeline")
public String healthTimeline(
        @RequestAttribute(
                value = "authenticatedUser",
                required = false)
        String email,
        Model model) {

    if (email == null) {
        return "redirect:/login";
    }

    User patient = userService.findByEmail(email);

    if (patient == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "patientName",
            patient.getName()
    );

    model.addAttribute(
            "patientEmail",
            patient.getEmail()
    );

    return "health-timeline";
}
}