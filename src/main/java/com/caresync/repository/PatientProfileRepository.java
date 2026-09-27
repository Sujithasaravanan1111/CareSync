package com.caresync.repository;

import com.caresync.entity.PatientProfile;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PatientProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public PatientProfile save(PatientProfile profile) {
        entityManager.persist(profile);
        return profile;
    }

    @Transactional
    public PatientProfile update(PatientProfile profile) {
        return entityManager.merge(profile);
    }

    public PatientProfile findByUser(User user) {
        return entityManager.createQuery(
                "SELECT p FROM PatientProfile p WHERE p.user = :user",
                PatientProfile.class
        )
        .setParameter("user", user)
        .getResultStream()
        .findFirst()
        .orElse(null);
    }
}