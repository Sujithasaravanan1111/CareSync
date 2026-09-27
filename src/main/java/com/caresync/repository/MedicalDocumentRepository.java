package com.caresync.repository;

import com.caresync.entity.MedicalDocument;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MedicalDocumentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MedicalDocument save(MedicalDocument document) {
        entityManager.persist(document);
        return document;
    }

    public MedicalDocument findById(Long id) {
        return entityManager.find(MedicalDocument.class, id);
    }

    public List<MedicalDocument> findByPatient(User patient) {
        return entityManager.createQuery(
                "SELECT d FROM MedicalDocument d " +
                "WHERE d.patient = :patient " +
                "ORDER BY d.uploadedAt DESC",
                MedicalDocument.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }

    @Transactional
    public void delete(MedicalDocument document) {
        entityManager.remove(
                entityManager.contains(document)
                        ? document
                        : entityManager.merge(document)
        );
    }
    public List<MedicalDocument> searchByText(
        User patient,
        String query) {

    return entityManager.createQuery(
            "SELECT d FROM MedicalDocument d " +
            "WHERE d.patient = :patient " +
            "AND LOWER(d.extractedText) LIKE :query " +
            "ORDER BY d.uploadedAt DESC",
            MedicalDocument.class
    )
    .setParameter("patient", patient)
    .setParameter("query", "%" + query.toLowerCase() + "%")
    .getResultList();
}
}