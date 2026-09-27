package com.caresync.dto;

import java.time.LocalDateTime;

public class MedicalDocumentResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
private String documentCategory;
private boolean aiClassified;
public String getDocumentCategory() {
    return documentCategory;
}

public void setDocumentCategory(String documentCategory) {
    this.documentCategory = documentCategory;
}

public boolean isAiClassified() {
    return aiClassified;
}

public void setAiClassified(boolean aiClassified) {
    this.aiClassified = aiClassified;
}
    public MedicalDocumentResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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