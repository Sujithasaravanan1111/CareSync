package com.caresync.service;

import com.caresync.entity.PharmacyInventory;
import com.caresync.repository.PharmacyInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PharmacyIntegrationService {

    private final PharmacyInventoryRepository inventoryRepository;

    public PharmacyIntegrationService(
            PharmacyInventoryRepository inventoryRepository) {

        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public PharmacyAvailabilityResult checkAvailability(
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
                inventoryRepository.findMedicineAtPharmacy(
                        pharmacyId,
                        medicineName
                );

        if (inventory == null) {

            return new PharmacyAvailabilityResult(
                    false,
                    "Medicine is not available at this pharmacy",
                    0,
                    null
            );
        }

        if (inventory.getAvailableQuantity()
                < requestedQuantity) {

            return new PharmacyAvailabilityResult(
                    false,
                    "Insufficient medicine quantity",
                    inventory.getAvailableQuantity(),
                    inventory.getPrice()
            );
        }

        return new PharmacyAvailabilityResult(
                true,
                "Medicine is available",
                inventory.getAvailableQuantity(),
                inventory.getPrice()
        );
    }

    public static class PharmacyAvailabilityResult {

        private final boolean available;
        private final String message;
        private final Integer availableQuantity;
        private final Double price;

        public PharmacyAvailabilityResult(
                boolean available,
                String message,
                Integer availableQuantity,
                Double price) {

            this.available = available;
            this.message = message;
            this.availableQuantity = availableQuantity;
            this.price = price;
        }

        public boolean isAvailable() {
            return available;
        }

        public String getMessage() {
            return message;
        }

        public Integer getAvailableQuantity() {
            return availableQuantity;
        }

        public Double getPrice() {
            return price;
        }
    }
}