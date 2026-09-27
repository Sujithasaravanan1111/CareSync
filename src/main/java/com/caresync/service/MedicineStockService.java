package com.caresync.service;

import com.caresync.entity.Medication;
import com.caresync.entity.MedicineStock;
import com.caresync.entity.User;
import com.caresync.repository.MedicationRepository;
import com.caresync.repository.MedicineStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineStockService {

    private final MedicineStockRepository stockRepository;
    private final MedicationRepository medicationRepository;
    private final UserService userService;

    public MedicineStockService(
            MedicineStockRepository stockRepository,
            MedicationRepository medicationRepository,
            UserService userService) {

        this.stockRepository = stockRepository;
        this.medicationRepository = medicationRepository;
        this.userService = userService;
    }

    @Transactional
    public MedicineStock createStock(
            String email,
            Long medicationId,
            Integer initialQuantity,
            Integer currentQuantity,
            Double dailyConsumption,
            Integer lowStockThreshold) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        Medication medication =
                medicationRepository.findById(medicationId);

        if (medication == null ||
                !medication.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Medication not found"
            );
        }

        if (stockRepository.findByMedicationId(
                medicationId) != null) {

            throw new RuntimeException(
                    "Stock already exists for this medication"
            );
        }

        validateQuantities(
                initialQuantity,
                currentQuantity,
                dailyConsumption,
                lowStockThreshold
        );

        MedicineStock stock =
                new MedicineStock();

        stock.setPatient(patient);
        stock.setMedication(medication);
        stock.setInitialQuantity(initialQuantity);
        stock.setCurrentQuantity(currentQuantity);
        stock.setDailyConsumption(dailyConsumption);
        stock.setLowStockThreshold(lowStockThreshold);

        return stockRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public List<MedicineStock> getStocks(String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        return stockRepository.findByPatient(patient);
    }

    @Transactional
    public MedicineStock updateStock(
            String email,
            Long id,
            Integer currentQuantity,
            Double dailyConsumption,
            Integer lowStockThreshold) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        MedicineStock stock =
                stockRepository.findById(id);

        if (stock == null ||
                !stock.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Stock not found"
            );
        }

        validateQuantities(
                stock.getInitialQuantity(),
                currentQuantity,
                dailyConsumption,
                lowStockThreshold
        );

        stock.setCurrentQuantity(currentQuantity);
        stock.setDailyConsumption(dailyConsumption);
        stock.setLowStockThreshold(lowStockThreshold);

        return stockRepository.update(stock);
    }

    @Transactional
    public void deleteStock(
            String email,
            Long id) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        MedicineStock stock =
                stockRepository.findById(id);

        if (stock == null ||
                !stock.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Stock not found"
            );
        }

        stockRepository.delete(stock);
    }

    @Transactional(readOnly = true)
    public List<MedicineStock> getLowStockAlerts(
            String email) {

        return getStocks(email)
                .stream()
                .filter(stock ->
                        stock.getCurrentQuantity()
                                <= stock.getLowStockThreshold())
                .collect(Collectors.toList());
    }

    private void validateQuantities(
            Integer initialQuantity,
            Integer currentQuantity,
            Double dailyConsumption,
            Integer lowStockThreshold) {

        if (initialQuantity == null ||
                initialQuantity <= 0) {

            throw new RuntimeException(
                    "Initial quantity must be greater than zero"
            );
        }

        if (currentQuantity == null ||
                currentQuantity < 0) {

            throw new RuntimeException(
                    "Current quantity cannot be negative"
            );
        }

        if (dailyConsumption == null ||
                dailyConsumption <= 0) {

            throw new RuntimeException(
                    "Daily consumption must be greater than zero"
            );
        }

        if (lowStockThreshold == null ||
                lowStockThreshold < 0) {

            throw new RuntimeException(
                    "Low-stock threshold cannot be negative"
            );
        }

        if (currentQuantity > initialQuantity) {

            throw new RuntimeException(
                    "Current quantity cannot exceed initial quantity"
            );
        }
    }
}
