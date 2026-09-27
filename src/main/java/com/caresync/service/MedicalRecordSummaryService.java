package com.caresync.service;

import org.springframework.stereotype.Service;

@Service
public class MedicalRecordSummaryService {

    public String generateSummary(String extractedText) {

        if (extractedText == null ||
                extractedText.trim().isEmpty()) {

            return "No readable medical information found.";
        }

        String text = extractedText.trim();

        StringBuilder summary =
                new StringBuilder();

        summary.append("Medical Record Summary\n\n");

        if (contains(text, "diagnosis")) {
            summary.append(
                    "Diagnosis information is present in the document.\n"
            );
        }

        if (contains(text, "medicine") ||
                contains(text, "medication") ||
                contains(text, "tablet")) {

            summary.append(
                    "Medication information is present in the document.\n"
            );
        }

        if (contains(text, "blood") ||
                contains(text, "hemoglobin") ||
                contains(text, "glucose") ||
                contains(text, "cholesterol")) {

            summary.append(
                    "Laboratory/test information is present in the document.\n"
            );
        }

        if (contains(text, "allergy") ||
                contains(text, "allergic")) {

            summary.append(
                    "Allergy information is present in the document.\n"
            );
        }

        if (summary.toString()
                .equals("Medical Record Summary\n\n")) {

            summary.append(
                    "The document contains medical information " +
                    "that requires further review."
            );
        }

        return summary.toString();
    }

    private boolean contains(
            String text,
            String keyword) {

        return text
                .toLowerCase()
                .contains(keyword.toLowerCase());
    }
}