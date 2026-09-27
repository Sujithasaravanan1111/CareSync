package com.caresync.repository;

import com.caresync.entity.Medication;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MedicationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Medication save(Medication medication) {
        entityManager.persist(medication);
        return medication;
    }

    @Transactional
    public Medication update(Medication medication) {
        return entityManager.merge(medication);
    }

    @Transactional
    public void delete(Medication medication) {
        entityManager.remove(
                entityManager.contains(medication)
                        ? medication
                        : entityManager.merge(medication)
        );
    }

    public Medication findById(Long id) {
        return entityManager.find(Medication.class, id);
    }

    public List<Medication> findByPatient(User patient) {
        return entityManager.createQuery(
                "SELECT m FROM Medication m " +
                "WHERE m.patient = :patient " +
                "ORDER BY m.startDate DESC",
                Medication.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }
}