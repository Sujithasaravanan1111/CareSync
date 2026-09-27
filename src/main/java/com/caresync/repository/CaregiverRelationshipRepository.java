package com.caresync.repository;

import com.caresync.entity.CaregiverRelationship;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CaregiverRelationshipRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public CaregiverRelationship save(
            CaregiverRelationship relationship) {

        entityManager.persist(relationship);
        return relationship;
    }

    @Transactional
    public CaregiverRelationship update(
            CaregiverRelationship relationship) {

        return entityManager.merge(relationship);
    }

    public CaregiverRelationship findById(Long id) {

        return entityManager.find(
                CaregiverRelationship.class,
                id
        );
    }

    public List<CaregiverRelationship> findByPatient(
            User patient) {

        return entityManager.createQuery(
                "SELECT c FROM CaregiverRelationship c " +
                "WHERE c.patient = :patient " +
                "ORDER BY c.id DESC",
                CaregiverRelationship.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }

    public List<CaregiverRelationship> findByCaregiver(
        User caregiver) {

    return entityManager.createQuery(
            "SELECT c FROM CaregiverRelationship c " +
            "WHERE LOWER(c.caregiverEmail) = LOWER(:email) " +
            "AND c.status = 'ACCEPTED' " +
            "ORDER BY c.id DESC",
            CaregiverRelationship.class
    )
    .setParameter("email", caregiver.getEmail())
    .getResultList();
}

    public CaregiverRelationship findByPatientAndEmail(
            User patient,
            String caregiverEmail) {

        List<CaregiverRelationship> results =
                entityManager.createQuery(
                        "SELECT c FROM CaregiverRelationship c " +
                        "WHERE c.patient = :patient " +
                        "AND LOWER(c.caregiverEmail) = LOWER(:email)",
                        CaregiverRelationship.class
                )
                .setParameter("patient", patient)
                .setParameter("email", caregiverEmail)
                .getResultList();

        return results.isEmpty()
                ? null
                : results.get(0);
    }
    public CaregiverRelationship findByCaregiverEmailAndPatientId(
        String caregiverEmail,
        Long patientId) {

    var results = entityManager.createQuery(
            "SELECT c FROM CaregiverRelationship c " +
            "WHERE LOWER(c.caregiverEmail) = LOWER(:email) " +
            "AND c.patient.id = :patientId " +
            "AND c.status = 'ACCEPTED'",
            CaregiverRelationship.class
    )
    .setParameter("email", caregiverEmail)
    .setParameter("patientId", patientId)
    .getResultList();

    return results.isEmpty() ? null : results.get(0);
}
}