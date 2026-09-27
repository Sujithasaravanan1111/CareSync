package com.caresync.service;

import com.caresync.entity.MedicalDocument;
import com.caresync.entity.User;
import com.caresync.repository.MedicalDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemanticHealthSearchService {

    private final MedicalDocumentRepository documentRepository;
    private final UserService userService;

    public SemanticHealthSearchService(
            MedicalDocumentRepository documentRepository,
            UserService userService) {

        this.documentRepository = documentRepository;
        this.userService = userService;
    }

    public List<MedicalDocument> search(
            String email,
            String query) {

        User patient = userService.findByEmail(email);

        if (patient == null) {
            throw new RuntimeException("User not found");
        }

        if (query == null || query.trim().isEmpty()) {
            throw new RuntimeException("Search query is required");
        }

        return documentRepository.searchByText(
                patient,
                query.trim()
        );
    }
}