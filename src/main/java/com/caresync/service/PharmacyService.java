package com.caresync.service;

import com.caresync.entity.Pharmacy;
import com.caresync.repository.PharmacyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    public PharmacyService(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    @Transactional
    public Pharmacy createPharmacy(
            String name,
            String address,
            String phone,
            boolean available) {

        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Pharmacy name is required");
        }

        if (address == null || address.trim().isEmpty()) {
            throw new RuntimeException("Pharmacy address is required");
        }

        Pharmacy pharmacy = new Pharmacy();

        pharmacy.setName(name);
        pharmacy.setAddress(address);
        pharmacy.setPhone(phone);
        pharmacy.setAvailable(available);

        return pharmacyRepository.save(pharmacy);
    }

    @Transactional(readOnly = true)
    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pharmacy getPharmacy(Long id) {

        Pharmacy pharmacy = pharmacyRepository.findById(id);

        if (pharmacy == null) {
            throw new RuntimeException("Pharmacy not found");
        }

        return pharmacy;
    }
}