package com.caresync.service;

import org.springframework.stereotype.Service;

@Service
public class DocumentClassificationService {

    public String classifyDocument(String extractedText) {

        if (extractedText == null ||
                extractedText.trim().isEmpty()) {

            return "OTHER";
        }

        String text =
                extractedText.toLowerCase();

        if (containsAny(
                text,
                "prescription",
                "rx",
                "dosage",
                "prescribed",
                "medicine",
                "tablet"
        )) {
            return "PRESCRIPTION";
        }

        if (containsAny(
                text,
                "laboratory",
                "lab report",
                "blood test",
                "hemoglobin",
                "glucose",
                "cholesterol",
                "test result"
        )) {
            return "LAB_REPORT";
        }

        if (containsAny(
                text,
                "discharge summary",
                "discharged",
                "hospital admission",
                "admission date",
                "discharge date"
        )) {
            return "DISCHARGE_SUMMARY";
        }

        if (containsAny(
                text,
                "medical record",
                "clinical history",
                "diagnosis",
                "patient history",
                "clinical notes"
        )) {
            return "MEDICAL_RECORD";
        }

        return "OTHER";
    }

    private boolean containsAny(
            String text,
            String... keywords) {

        for (String keyword : keywords) {

            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}