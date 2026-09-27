package com.caresync.repository;

import com.caresync.entity.PharmacyInventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PharmacyInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public PharmacyInventory save(
            PharmacyInventory inventory) {

        entityManager.persist(inventory);
        return inventory;
    }

    @Transactional
    public PharmacyInventory update(
            PharmacyInventory inventory) {

        return entityManager.merge(inventory);
    }

    public PharmacyInventory findById(Long id) {
        return entityManager.find(
                PharmacyInventory.class,
                id
        );
    }

    public List<PharmacyInventory> findByPharmacy(
            Long pharmacyId) {

        return entityManager.createQuery(
                "SELECT i FROM PharmacyInventory i " +
                "WHERE i.pharmacy.id = :pharmacyId " +
                "ORDER BY i.medicineName",
                PharmacyInventory.class
        )
        .setParameter("pharmacyId", pharmacyId)
        .getResultList();
    }

    public PharmacyInventory findMedicineAtPharmacy(
            Long pharmacyId,
            String medicineName) {

        List<PharmacyInventory> results =
                entityManager.createQuery(
                        "SELECT i FROM PharmacyInventory i " +
                        "WHERE i.pharmacy.id = :pharmacyId " +
                        "AND LOWER(i.medicineName) = LOWER(:medicineName)",
                        PharmacyInventory.class
                )
                .setParameter("pharmacyId", pharmacyId)
                .setParameter("medicineName", medicineName)
                .getResultList();

        return results.isEmpty()
                ? null
                : results.get(0);
    }
}