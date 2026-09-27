package com.caresync.service;

import com.caresync.entity.MedicalDocument;
import com.caresync.entity.User;
import com.caresync.repository.MedicalDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MedicalDocumentService {
    private final DocumentClassificationService classificationService;
private final DocumentTextExtractionService textExtractionService;
private final MedicalDocumentRepository documentRepository;
private final UserService userService;
    private final Path uploadDirectory =
            Paths.get("caresync-uploads");


    public MedicalDocumentService(
        MedicalDocumentRepository documentRepository,
        UserService userService,
        DocumentClassificationService classificationService,
        DocumentTextExtractionService textExtractionService) {
        this.classificationService = classificationService;
this.textExtractionService = textExtractionService;
this.documentRepository = documentRepository;
this.userService = userService;

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to create upload directory"
            );
        }
    }

    @Transactional
    public MedicalDocument uploadDocument(
            String email,
            MultipartFile file) throws IOException {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null ||
                originalName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Invalid file name"
            );
        }

        String safeFileName =
                UUID.randomUUID() + "_" +
                Paths.get(originalName)
                        .getFileName()
                        .toString();

        Path targetPath =
                uploadDirectory.resolve(safeFileName);

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        MedicalDocument document =
                new MedicalDocument();

        document.setPatient(patient);
        document.setFileName(originalName);
        document.setFileType(
                file.getContentType() != null
                        ? file.getContentType()
                        : "application/octet-stream"
        );
        document.setFilePath(
                targetPath.toAbsolutePath().toString()
        );
        document.setFileSize(file.getSize());
document.setUploadedAt(LocalDateTime.now());

// Extract actual text from the uploaded document
String extractedText =
        textExtractionService.extractText(targetPath);
document.setExtractedText(extractedText);
// Classify the actual document content
String category =
        classificationService.classifyDocument(
                extractedText
        );

document.setDocumentCategory(category);
document.setAiClassified(true);

return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public List<MedicalDocument> getDocuments(
            String email) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        return documentRepository.findByPatient(patient);
    }

    @Transactional(readOnly = true)
    public MedicalDocument getDocument(
            String email,
            Long id) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        MedicalDocument document =
                documentRepository.findById(id);

        if (document == null ||
                !document.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Document not found"
            );
        }

        return document;
    }

    @Transactional
    public void deleteDocument(
            String email,
            Long id) {

        MedicalDocument document =
                getDocument(email, id);

        try {
            Files.deleteIfExists(
                    Paths.get(document.getFilePath())
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to delete file"
            );
        }

        documentRepository.delete(document);
    }
}