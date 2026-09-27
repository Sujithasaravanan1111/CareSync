package com.caresync.service;

import com.caresync.entity.Pharmacy;
import com.caresync.entity.PharmacyInventory;
import com.caresync.repository.PharmacyInventoryRepository;
import com.caresync.repository.PharmacyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PharmacyInventoryService {

    private final PharmacyInventoryRepository inventoryRepository;
    private final PharmacyRepository pharmacyRepository;

    public PharmacyInventoryService(
            PharmacyInventoryRepository inventoryRepository,
            PharmacyRepository pharmacyRepository) {

        this.inventoryRepository = inventoryRepository;
        this.pharmacyRepository = pharmacyRepository;
    }

    @Transactional
    public PharmacyInventory addInventory(
            Long pharmacyId,
            String medicineName,
            Integer availableQuantity,
            Double price) {

        Pharmacy pharmacy =
                pharmacyRepository.findById(pharmacyId);

        if (pharmacy == null) {
            throw new RuntimeException("Pharmacy not found");
        }

        if (medicineName == null ||
                medicineName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Medicine name is required"
            );
        }

        if (availableQuantity == null ||
                availableQuantity < 0) {

            throw new RuntimeException(
                    "Available quantity cannot be negative"
            );
        }

        if (price == null || price < 0) {

            throw new RuntimeException(
                    "Price cannot be negative"
            );
        }

        PharmacyInventory existing =
                inventoryRepository.findMedicineAtPharmacy(
                        pharmacyId,
                        medicineName
                );

        if (existing != null) {

            throw new RuntimeException(
                    "Medicine already exists in this pharmacy inventory"
            );
        }

        PharmacyInventory inventory =
                new PharmacyInventory();

        inventory.setPharmacy(pharmacy);
        inventory.setMedicineName(medicineName);
        inventory.setAvailableQuantity(
                availableQuantity
        );
        inventory.setPrice(price);

        return inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    public List<PharmacyInventory> getInventory(
            Long pharmacyId) {

        Pharmacy pharmacy =
                pharmacyRepository.findById(pharmacyId);

        if (pharmacy == null) {
            throw new RuntimeException("Pharmacy not found");
        }

        return inventoryRepository.findByPharmacy(
                pharmacyId
        );
    }

    @Transactional
    public PharmacyInventory updateInventory(
            Long inventoryId,
            Integer availableQuantity,
            Double price) {

        PharmacyInventory inventory =
                inventoryRepository.findById(
                        inventoryId
                );

        if (inventory == null) {
            throw new RuntimeException(
                    "Inventory item not found"
            );
        }

        if (availableQuantity == null ||
                availableQuantity < 0) {

            throw new RuntimeException(
                    "Available quantity cannot be negative"
            );
        }

        if (price == null || price < 0) {

            throw new RuntimeException(
                    "Price cannot be negative"
            );
        }

        inventory.setAvailableQuantity(
                availableQuantity
        );

        inventory.setPrice(price);

        return inventoryRepository.update(
                inventory
        );
    }

    @Transactional(readOnly = true)
    public PharmacyInventory checkAvailability(
            Long pharmacyId,
            String medicineName,
            Integer requestedQuantity) {

        if (requestedQuantity == null ||
                requestedQuantity <= 0) {

            throw new RuntimeException(
                    "Requested quantity must be greater than zero"
            );
        }

        PharmacyInventory inventory =
                inventoryRepository
                        .findMedicineAtPharmacy(
                                pharmacyId,
                                medicineName
                        );

        if (inventory == null) {
            throw new RuntimeException(
                    "Medicine not available at this pharmacy"
            );
        }

        if (inventory.getAvailableQuantity()
                < requestedQuantity) {

            throw new RuntimeException(
                    "Insufficient medicine quantity"
            );
        }

        return inventory;
    }
}