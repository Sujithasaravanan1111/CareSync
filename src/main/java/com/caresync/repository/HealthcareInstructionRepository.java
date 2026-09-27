package com.caresync.repository;

import com.caresync.entity.HealthcareInstruction;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class HealthcareInstructionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public HealthcareInstruction save(
            HealthcareInstruction instruction) {

        entityManager.persist(instruction);
        return instruction;
    }

    
    public HealthcareInstruction findById(Long id) {
        return entityManager.find(
                HealthcareInstruction.class,
                id
        );
    }

    public List<HealthcareInstruction> findByPatient(
            User patient) {

        return entityManager.createQuery(
                "SELECT h FROM HealthcareInstruction h " +
                "WHERE h.patient = :patient " +
                "ORDER BY h.createdAt DESC",
                HealthcareInstruction.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }

    public List<HealthcareInstruction> findByMedication(
            Long medicationId) {

        return entityManager.createQuery(
                "SELECT h FROM HealthcareInstruction h " +
                "WHERE h.medication.id = :medicationId " +
                "ORDER BY h.version ASC",
                HealthcareInstruction.class
        )
        .setParameter("medicationId", medicationId)
        .getResultList();
    }
    @Transactional
public HealthcareInstruction update(
        HealthcareInstruction instruction) {

    return entityManager.merge(instruction);
}
}