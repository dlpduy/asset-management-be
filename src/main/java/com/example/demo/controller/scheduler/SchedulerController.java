package com.example.demo.controller.scheduler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResponseObject;
import com.example.demo.service.EvaluationReminderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final EvaluationReminderService evaluationReminderService;

    @PostMapping("/trigger-evaluation-reminders")
    public ResponseEntity<ResponseObject> triggerEvaluationReminders() {
        int dispatched = evaluationReminderService.dispatchUpcomingEvaluationReminders();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Evaluation reminder job executed")
                .data(dispatched)
                .build());
    }
}
