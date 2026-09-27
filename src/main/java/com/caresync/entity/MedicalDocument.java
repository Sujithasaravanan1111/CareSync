package com.caresync.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_documents")
public class MedicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    @Column(name = "document_category")
private String documentCategory;
@Column(name = "extracted_text", columnDefinition = "LONGTEXT")
private String extractedText;
public String getExtractedText() {
    return extractedText;
}

public void setExtractedText(String extractedText) {
    this.extractedText = extractedText;
}
@Column(name = "ai_classified")
private Boolean aiClassified;
public String getDocumentCategory() {
    return documentCategory;
}

public void setDocumentCategory(String documentCategory) {
    this.documentCategory = documentCategory;
}

public Boolean getAiClassified() {
    return aiClassified;
}

public void setAiClassified(Boolean aiClassified) {
    this.aiClassified = aiClassified;
}

    public MedicalDocument() {
    }

    public Long getId() {
        return id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}