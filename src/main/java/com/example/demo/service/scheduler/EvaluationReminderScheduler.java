package com.example.demo.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.service.EvaluationReminderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EvaluationReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(EvaluationReminderScheduler.class);

    private final EvaluationReminderService evaluationReminderService;

    @Value("${app.notification.evaluation.cron:0 0 1 * * ?}")
    private String cronExpression;

    @Scheduled(cron = "${app.notification.evaluation.cron:0 0 1 * * ?}")
    public void run() {
        int dispatched = evaluationReminderService.dispatchUpcomingEvaluationReminders();
        log.info("Scheduled evaluation reminder run finished (cron: {}). Notifications dispatched: {}", cronExpression,
                dispatched);
    }
}
