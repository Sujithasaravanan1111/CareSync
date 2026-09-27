package com.caresync.controller;

import com.caresync.dto.MedicalDocumentResponse;
import com.caresync.entity.MedicalDocument;
import com.caresync.service.MedicalDocumentService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient/documents")
public class MedicalDocumentController {

    private final MedicalDocumentService documentService;

    public MedicalDocumentController(
            MedicalDocumentService documentService) {

        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email,
            @RequestParam("file")
            MultipartFile file) {

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        try {

            MedicalDocument saved =
                    documentService.uploadDocument(
                            email,
                            file
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(saved));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getDocuments(
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

            List<MedicalDocumentResponse> documents =
                    documentService
                            .getDocuments(email)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());

            return ResponseEntity.ok(documents);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadDocument(
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

            MedicalDocument document =
                    documentService.getDocument(
                            email,
                            id
                    );

            Resource resource =
                    new FileSystemResource(
                            document.getFilePath()
                    );

            if (!resource.exists()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("File not found");
            }

            MediaType mediaType;

            try {
                mediaType =
                        MediaType.parseMediaType(
                                document.getFileType()
                        );
            } catch (Exception e) {
                mediaType =
                        MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    document.getFileName() +
                                    "\""
                    )
                    .body(resource);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(
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

            documentService.deleteDocument(
                    email,
                    id
            );

            return ResponseEntity.ok(
                    "Document deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
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
        Boolean.TRUE.equals(document.getAiClassified())
);

        return response;
    }
}