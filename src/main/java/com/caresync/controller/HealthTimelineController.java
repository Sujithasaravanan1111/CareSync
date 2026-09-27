package com.caresync.controller;

import com.caresync.dto.HealthTimelineEvent;
import com.caresync.service.HealthTimelineService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/timeline")
public class HealthTimelineController {

    private final HealthTimelineService timelineService;

    public HealthTimelineController(
            HealthTimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping
    public ResponseEntity<?> getTimeline(
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
            List<HealthTimelineEvent> timeline =
                    timelineService.getTimeline(email);

            return ResponseEntity.ok(timeline);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}