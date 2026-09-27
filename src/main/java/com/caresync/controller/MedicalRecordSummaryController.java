package com.caresync.controller;
import com.caresync.service.AiSummaryService;
import com.caresync.entity.MedicalDocument;
import com.caresync.service.DocumentTextExtractionService;
import com.caresync.service.MedicalDocumentService;
import com.caresync.service.MedicalRecordSummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;

@RestController
@RequestMapping("/api/patient/documents")
public class MedicalRecordSummaryController {

    private final MedicalDocumentService documentService;
    private final DocumentTextExtractionService textExtractionService;
    private final AiSummaryService aiSummaryService;
    public MedicalRecordSummaryController(
            MedicalDocumentService documentService,
            DocumentTextExtractionService textExtractionService,
            AiSummaryService aiSummaryService) {

        this.documentService = documentService;
        this.textExtractionService = textExtractionService;
        this.aiSummaryService = aiSummaryService;
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<?> generateSummary(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,

            @PathVariable("id")
            Long id) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            MedicalDocument document =
                    documentService.getDocument(
                            email,
                            id
                    );

            String extractedText =
                    textExtractionService.extractText(
                            Paths.get(
                                    document.getFilePath()
                            )
                    );

            String summary =
        aiSummaryService.generateSummary(
                extractedText
        );

            return ResponseEntity.ok(
                    new SummaryResponse(
                            document.getId(),
                            document.getFileName(),
                            document.getDocumentCategory(),
                            summary
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    public static class SummaryResponse {

        private Long documentId;
        private String fileName;
        private String documentCategory;
        private String summary;
        private String disclaimer;

        public SummaryResponse(
        Long documentId,
        String fileName,
        String documentCategory,
        String summary) {

    this.documentId = documentId;
    this.fileName = fileName;
    this.documentCategory =
            documentCategory;
    this.summary = summary;

    this.disclaimer =
            "This AI-generated summary is for informational purposes only " +
            "and is not a substitute for professional medical advice, " +
            "diagnosis, or treatment.";
}
                public Long getDocumentId() {
                        return documentId;
                }

        public String getFileName() {
            return fileName;
        }

        public String getDocumentCategory() {
            return documentCategory;
        }

        public String getSummary() {
            return summary;
        }
        public String getDisclaimer() {
    return disclaimer;
}
    }
}