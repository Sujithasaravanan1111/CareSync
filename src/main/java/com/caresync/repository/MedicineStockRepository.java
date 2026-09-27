package com.caresync.repository;

import com.caresync.entity.MedicineStock;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MedicineStockRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MedicineStock save(MedicineStock stock) {
        entityManager.persist(stock);
        return stock;
    }

    @Transactional
    public MedicineStock update(MedicineStock stock) {
        return entityManager.merge(stock);
    }

    @Transactional
    public void delete(MedicineStock stock) {
        entityManager.remove(
                entityManager.contains(stock)
                        ? stock
                        : entityManager.merge(stock)
        );
    }

    public MedicineStock findById(Long id) {
        return entityManager.find(MedicineStock.class, id);
    }

    public MedicineStock findByMedicationId(Long medicationId) {
        List<MedicineStock> results = entityManager.createQuery(
                "SELECT s FROM MedicineStock s " +
                "WHERE s.medication.id = :medicationId",
                MedicineStock.class
        )
        .setParameter("medicationId", medicationId)
        .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    public List<MedicineStock> findByPatient(User patient) {
        return entityManager.createQuery(
                "SELECT s FROM MedicineStock s " +
                "WHERE s.patient = :patient " +
                "ORDER BY s.id DESC",
                MedicineStock.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }
}