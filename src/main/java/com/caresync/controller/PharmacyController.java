package com.caresync.controller;

import com.caresync.entity.Pharmacy;
import com.caresync.entity.PharmacyInventory;
import com.caresync.service.PharmacyInventoryService;
import com.caresync.service.PharmacyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyController {

    private final PharmacyService pharmacyService;
    private final PharmacyInventoryService inventoryService;

    public PharmacyController(
            PharmacyService pharmacyService,
            PharmacyInventoryService inventoryService) {

        this.pharmacyService = pharmacyService;
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<?> createPharmacy(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam(value = "phone", required = false)
            String phone,
            @RequestParam("available") boolean available) {

        try {

            Pharmacy pharmacy =
                    pharmacyService.createPharmacy(
                            name,
                            address,
                            phone,
                            available
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(pharmacy);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPharmacies() {

        try {

            List<Pharmacy> pharmacies =
                    pharmacyService.getAllPharmacies();

            return ResponseEntity.ok(pharmacies);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPharmacy(
            @PathVariable("id") Long id) {

        try {

            return ResponseEntity.ok(
                    pharmacyService.getPharmacy(id)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/{pharmacyId}/inventory")
    public ResponseEntity<?> addInventory(
            @PathVariable("pharmacyId") Long pharmacyId,
            @RequestParam("medicineName")
            String medicineName,
            @RequestParam("availableQuantity")
            Integer availableQuantity,
            @RequestParam("price")
            Double price) {

        try {

            PharmacyInventory inventory =
                    inventoryService.addInventory(
                            pharmacyId,
                            medicineName,
                            availableQuantity,
                            price
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(inventory);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{pharmacyId}/inventory")
    public ResponseEntity<?> getInventory(
            @PathVariable("pharmacyId") Long pharmacyId) {

        try {

            return ResponseEntity.ok(
                    inventoryService.getInventory(
                            pharmacyId
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/inventory/{inventoryId}")
    public ResponseEntity<?> updateInventory(
            @PathVariable("inventoryId")
            Long inventoryId,
            @RequestParam("availableQuantity")
            Integer availableQuantity,
            @RequestParam("price")
            Double price) {

        try {

            return ResponseEntity.ok(
                    inventoryService.updateInventory(
                            inventoryId,
                            availableQuantity,
                            price
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{pharmacyId}/availability")
    public ResponseEntity<?> checkAvailability(
            @PathVariable("pharmacyId") Long pharmacyId,
            @RequestParam("medicineName")
            String medicineName,
            @RequestParam("requestedQuantity")
            Integer requestedQuantity) {

        try {

            PharmacyInventory inventory =
                    inventoryService.checkAvailability(
                            pharmacyId,
                            medicineName,
                            requestedQuantity
                    );

            return ResponseEntity.ok(inventory);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}