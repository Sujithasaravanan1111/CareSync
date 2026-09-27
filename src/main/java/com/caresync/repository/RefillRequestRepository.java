package com.caresync.repository;

import com.caresync.entity.RefillRequest;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RefillRequestRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public RefillRequest save(RefillRequest request) {
        entityManager.persist(request);
        return request;
    }

    @Transactional
    public RefillRequest update(RefillRequest request) {
        return entityManager.merge(request);
    }

    public RefillRequest findById(Long id) {
        return entityManager.find(
                RefillRequest.class,
                id
        );
    }

    public List<RefillRequest> findByPatient(User patient) {
        return entityManager.createQuery(
                "SELECT r FROM RefillRequest r " +
                "WHERE r.patient = :patient " +
                "ORDER BY r.createdAt DESC",
                RefillRequest.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }
}