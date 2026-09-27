package com.caresync.service;

import com.caresync.entity.Medication;
import com.caresync.entity.MedicationReminder;
import com.caresync.entity.User;
import com.caresync.repository.MedicationReminderRepository;
import com.caresync.repository.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MedicationReminderService {

    private final MedicationReminderRepository reminderRepository;
    private final MedicationRepository medicationRepository;
    private final UserService userService;

    public MedicationReminderService(
            MedicationReminderRepository reminderRepository,
            MedicationRepository medicationRepository,
            UserService userService) {

        this.reminderRepository = reminderRepository;
        this.medicationRepository = medicationRepository;
        this.userService = userService;
    }

    @Transactional
    public MedicationReminder createReminder(
            String email,
            Long medicationId,
            LocalDate date,
            LocalTime time) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
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

        if (date == null || time == null) {
            throw new RuntimeException(
                    "Reminder date and time are required"
            );
        }

        MedicationReminder reminder =
                new MedicationReminder();

        reminder.setPatient(patient);
        reminder.setMedication(medication);
        reminder.setReminderDate(date);
        reminder.setReminderTime(time);
        reminder.setStatus("SCHEDULED");

        return reminderRepository.save(reminder);
    }

    @Transactional(readOnly = true)
    public List<MedicationReminder> getReminders(
            String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        return reminderRepository.findByPatient(patient);
    }

    @Transactional
    public MedicationReminder updateStatus(
            String email,
            Long id,
            String status,
            LocalTime snoozedUntil) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        MedicationReminder reminder =
                reminderRepository.findById(id);

        if (reminder == null ||
                !reminder.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Reminder not found"
            );
        }

        if (!status.equals("TAKEN") &&
                !status.equals("MISSED") &&
                !status.equals("SKIPPED") &&
                !status.equals("SNOOZED") &&
                !status.equals("SCHEDULED")) {

            throw new RuntimeException(
                    "Invalid reminder status"
            );
        }

        reminder.setStatus(status);

        if (status.equals("SNOOZED")) {

            if (snoozedUntil == null) {
                throw new RuntimeException(
                        "Snooze time is required"
                );
            }

            reminder.setSnoozedUntil(snoozedUntil);

        } else {
            reminder.setSnoozedUntil(null);
        }

        return reminderRepository.update(reminder);
    }

    @Transactional
    public void deleteReminder(
            String email,
            Long id) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        MedicationReminder reminder =
                reminderRepository.findById(id);

        if (reminder == null ||
                !reminder.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Reminder not found"
            );
        }

        reminderRepository.delete(reminder);
    }
    @Transactional(readOnly = true)
public java.util.Map<String, Object> getAdherenceStats(
        String email) {

    User patient = userService.findByEmail(email);

    if (patient == null) {
        throw new RuntimeException("User not found");
    }

    List<MedicationReminder> reminders =
            reminderRepository.findByPatient(patient);

    long total = reminders.size();

    long taken = reminders.stream()
            .filter(r -> "TAKEN".equals(r.getStatus()))
            .count();

    long missed = reminders.stream()
            .filter(r -> "MISSED".equals(r.getStatus()))
            .count();

    long skipped = reminders.stream()
            .filter(r -> "SKIPPED".equals(r.getStatus()))
            .count();

    long snoozed = reminders.stream()
            .filter(r -> "SNOOZED".equals(r.getStatus()))
            .count();

    double adherencePercentage =
            total == 0
                    ? 0.0
                    : (taken * 100.0) / total;

    java.util.Map<String, Object> stats =
            new java.util.LinkedHashMap<>();

    stats.put("totalReminders", total);
    stats.put("taken", taken);
    stats.put("missed", missed);
    stats.put("skipped", skipped);
    stats.put("snoozed", snoozed);
    stats.put("adherencePercentage",
        Math.round(adherencePercentage * 100.0) / 100.0);

String insight;

if (total == 0) {
    insight = "No medication reminder data is available yet.";
} else if (adherencePercentage >= 80) {
    insight = "Medication adherence is good.";
} else if (adherencePercentage >= 50) {
    insight = "Medication adherence is moderate. Try to follow the medication schedule more consistently.";
} else {
    insight = "Medication adherence is low. Review missed or skipped reminders and follow the prescribed schedule.";
}

stats.put("insight", insight);

return stats;
}
}