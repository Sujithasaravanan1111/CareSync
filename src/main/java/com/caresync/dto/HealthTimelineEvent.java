package com.caresync.dto;

import java.time.LocalDateTime;

public class HealthTimelineEvent {

    private LocalDateTime date;
    private String type;
    private String title;
    private String description;

    public HealthTimelineEvent() {
    }

    public HealthTimelineEvent(
            LocalDateTime date,
            String type,
            String title,
            String description) {

        this.date = date;
        this.type = type;
        this.title = title;
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}