package com.caresync.service;

import com.caresync.entity.MedicalRecord;
import com.caresync.entity.User;
import com.caresync.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final UserService userService;

    public MedicalRecordService(
            MedicalRecordRepository recordRepository,
            UserService userService) {

        this.recordRepository = recordRepository;
        this.userService = userService;
    }

    @Transactional
    public MedicalRecord createRecord(
            String email,
            MedicalRecord record) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        record.setPatient(patient);

        return recordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecord> getRecords(String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        return recordRepository.findByPatient(patient);
    }

    @Transactional
    public MedicalRecord updateRecord(
            String email,
            Long id,
            MedicalRecord data) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        MedicalRecord existing =
                recordRepository.findById(id);

        if (existing == null ||
                !existing.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Medical record not found"
            );
        }

        existing.setDoctorName(data.getDoctorName());
        existing.setRecordDate(data.getRecordDate());
        existing.setDiagnosis(data.getDiagnosis());
        existing.setDescription(data.getDescription());
        existing.setTreatment(data.getTreatment());

        return recordRepository.update(existing);
    }

    @Transactional
    public void deleteRecord(
            String email,
            Long id) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        MedicalRecord record =
                recordRepository.findById(id);

        if (record == null ||
                !record.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Medical record not found"
            );
        }

        recordRepository.delete(record);
    }
}