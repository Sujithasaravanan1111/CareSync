package com.caresync.repository;

import com.caresync.entity.MedicalRecord;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MedicalRecordRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MedicalRecord save(MedicalRecord record) {
        entityManager.persist(record);
        return record;
    }

    @Transactional
    public MedicalRecord update(MedicalRecord record) {
        return entityManager.merge(record);
    }

    @Transactional
    public void delete(MedicalRecord record) {
        entityManager.remove(
                entityManager.contains(record)
                        ? record
                        : entityManager.merge(record)
        );
    }

    public MedicalRecord findById(Long id) {
        return entityManager.find(MedicalRecord.class, id);
    }

    public List<MedicalRecord> findByPatient(User patient) {
        return entityManager.createQuery(
                "SELECT m FROM MedicalRecord m " +
                "WHERE m.patient = :patient " +
                "ORDER BY m.recordDate DESC",
                MedicalRecord.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }
}