package com.caresync.dto;

import java.time.LocalDateTime;

public class CaregiverDocumentResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String documentCategory;
    private Boolean aiClassified;

    public CaregiverDocumentResponse() {
    }

    public CaregiverDocumentResponse(
            Long id,
            String fileName,
            String fileType,
            Long fileSize,
            LocalDateTime uploadedAt,
            String documentCategory,
            Boolean aiClassified) {

        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.documentCategory = documentCategory;
        this.aiClassified = aiClassified;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public Boolean getAiClassified() {
        return aiClassified;
    }
}