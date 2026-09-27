package com.caresync.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class AiSummaryService {

    private final Client client;

    public AiSummaryService() {
        this.client = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();
    }

    public String generateSummary(String medicalText) {

        if (medicalText == null || medicalText.trim().isEmpty()) {
            return "No medical text was available for summarization.";
        }

        String prompt = """
                You are a medical document summarization assistant.

                Summarize the following medical document clearly and safely.

                Include:
                1. Document type
                2. Important findings
                3. Diagnoses or medical conditions mentioned
                4. Medications mentioned
                5. Tests and results
                6. Allergies, if mentioned
                7. Important follow-up information

                Do not invent information that is not present in the document.
                Do not provide a new diagnosis or change medication.
                Clearly state when information is not mentioned.

                Medical document:

                """ + medicalText;

        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            null
                    );

            return response.text();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to generate AI summary: " +
                    e.getMessage()
            );
        }
    }
}