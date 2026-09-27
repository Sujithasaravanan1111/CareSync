package com.caresync.controller;

import com.caresync.dto.MedicineStockResponse;
import com.caresync.entity.MedicineStock;
import com.caresync.service.MedicineStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/stock")
public class MedicineStockController {

    private final MedicineStockService stockService;

    public MedicineStockController(
            MedicineStockService stockService) {

        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<?> createStock(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @RequestParam("medicationId")
            Long medicationId,
            @RequestParam("initialQuantity")
            Integer initialQuantity,
            @RequestParam("currentQuantity")
            Integer currentQuantity,
            @RequestParam("dailyConsumption")
            Double dailyConsumption,
            @RequestParam("lowStockThreshold")
            Integer lowStockThreshold) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            MedicineStock stock =
                    stockService.createStock(
                            email,
                            medicationId,
                            initialQuantity,
                            currentQuantity,
                            dailyConsumption,
                            lowStockThreshold
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(stock));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getStocks(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            List<MedicineStockResponse> stocks =
                    stockService.getStocks(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(stocks);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStock(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @PathVariable("id") Long id,
            @RequestParam("currentQuantity")
            Integer currentQuantity,
            @RequestParam("dailyConsumption")
            Double dailyConsumption,
            @RequestParam("lowStockThreshold")
            Integer lowStockThreshold) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            MedicineStock updated =
                    stockService.updateStock(
                            email,
                            id,
                            currentQuantity,
                            dailyConsumption,
                            lowStockThreshold
                    );

            return ResponseEntity.ok(
                    toResponse(updated)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<?> getLowStockAlerts(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            List<MedicineStockResponse> alerts =
                    stockService.getLowStockAlerts(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(alerts);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStock(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @PathVariable("id") Long id) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            stockService.deleteStock(
                    email,
                    id
            );

            return ResponseEntity.ok(
                    "Medicine stock deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private MedicineStockResponse toResponse(
            MedicineStock stock) {

        MedicineStockResponse response =
                new MedicineStockResponse();

        response.setId(stock.getId());

        response.setMedicationId(
                stock.getMedication().getId()
        );

        response.setMedicineName(
                stock.getMedication().getMedicineName()
        );

        response.setInitialQuantity(
                stock.getInitialQuantity()
        );

        response.setCurrentQuantity(
                stock.getCurrentQuantity()
        );

        response.setDailyConsumption(
                stock.getDailyConsumption()
        );

        response.setLowStockThreshold(
                stock.getLowStockThreshold()
        );

        double remainingDays =
                stock.getDailyConsumption() > 0
                        ? stock.getCurrentQuantity()
                            / stock.getDailyConsumption()
                        : 0;

        response.setEstimatedRemainingDays(
                Math.round(remainingDays * 100.0) / 100.0
        );

        response.setLowStock(
                stock.getCurrentQuantity()
                        <= stock.getLowStockThreshold()
        );

        return response;
    }
}