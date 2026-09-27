package com.caresync.repository;

import com.caresync.entity.Pharmacy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PharmacyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Pharmacy save(Pharmacy pharmacy) {
        entityManager.persist(pharmacy);
        return pharmacy;
    }

    public Pharmacy findById(Long id) {
        return entityManager.find(Pharmacy.class, id);
    }

    public List<Pharmacy> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM Pharmacy p ORDER BY p.name",
                Pharmacy.class
        ).getResultList();
    }
}