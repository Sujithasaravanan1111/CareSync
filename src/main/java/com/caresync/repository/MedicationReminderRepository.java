package com.caresync.repository;

import com.caresync.entity.MedicationReminder;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MedicationReminderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MedicationReminder save(MedicationReminder reminder) {
        entityManager.persist(reminder);
        return reminder;
    }

    @Transactional
    public MedicationReminder update(MedicationReminder reminder) {
        return entityManager.merge(reminder);
    }

    @Transactional
    public void delete(MedicationReminder reminder) {
        entityManager.remove(
                entityManager.contains(reminder)
                        ? reminder
                        : entityManager.merge(reminder)
        );
    }

    public MedicationReminder findById(Long id) {
        return entityManager.find(
                MedicationReminder.class,
                id
        );
    }

    public List<MedicationReminder> findByPatient(
            User patient) {

        return entityManager.createQuery(
                "SELECT r FROM MedicationReminder r " +
                "WHERE r.patient = :patient " +
                "ORDER BY r.reminderDate ASC, r.reminderTime ASC",
                MedicationReminder.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }
}