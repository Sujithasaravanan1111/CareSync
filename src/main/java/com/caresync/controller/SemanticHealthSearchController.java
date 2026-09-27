package com.caresync.controller;

import com.caresync.dto.MedicalDocumentResponse;
import com.caresync.entity.MedicalDocument;
import com.caresync.service.SemanticHealthSearchService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/documents")
public class SemanticHealthSearchController {

    private final SemanticHealthSearchService searchService;

    public SemanticHealthSearchController(
            SemanticHealthSearchService searchService) {

        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDocuments(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @RequestParam("query") String query) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            List<MedicalDocumentResponse> results =
                    searchService.search(email, query)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(results);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    private MedicalDocumentResponse toResponse(
            MedicalDocument document) {

        MedicalDocumentResponse response =
                new MedicalDocumentResponse();

        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setFileType(document.getFileType());
        response.setFileSize(document.getFileSize());
        response.setUploadedAt(document.getUploadedAt());
        response.setDocumentCategory(
                document.getDocumentCategory()
        );
        response.setAiClassified(
                Boolean.TRUE.equals(
                        document.getAiClassified()
                )
        );

        return response;
    }
}