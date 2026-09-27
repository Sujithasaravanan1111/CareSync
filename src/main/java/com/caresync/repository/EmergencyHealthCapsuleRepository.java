package com.caresync.repository;

import com.caresync.entity.EmergencyHealthCapsule;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EmergencyHealthCapsuleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public EmergencyHealthCapsule save(
            EmergencyHealthCapsule capsule) {

        entityManager.persist(capsule);
        return capsule;
    }

    @Transactional
    public EmergencyHealthCapsule update(
            EmergencyHealthCapsule capsule) {

        return entityManager.merge(capsule);
    }

    public EmergencyHealthCapsule findByPatient(
            User patient) {

        var results = entityManager.createQuery(
                "SELECT e FROM EmergencyHealthCapsule e " +
                "WHERE e.patient = :patient",
                EmergencyHealthCapsule.class
        )
        .setParameter("patient", patient)
        .getResultList();

        return results.isEmpty()
                ? null
                : results.get(0);
    }
}   