package com.example.demo.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.notification.NotificationCommand;
import com.example.demo.entity.Asset;
import com.example.demo.entity.NotificationMarker;
import com.example.demo.entity.User;
import com.example.demo.enums.AssetStatus;
import com.example.demo.enums.NotificationType;
import com.example.demo.enums.Role;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.NotificationMarkerRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluationReminderService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationReminderService.class);

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final NotificationMarkerRepository markerRepository;
    private final NotificationService notificationService;

    @Value("${app.notification.evaluation.lead-days:30}")
    private int leadDays;

    @Value("${app.notification.evaluation.cycle-years:1}")
    private int cycleYears;

    @Value("${app.notification.evaluation.timezone:UTC}")
    private String timezone;

    /**
     * Scans assets and dispatches reminders for evaluations that are approaching.
     *
     * @return number of notifications created
     */
    @Transactional
    public int dispatchUpcomingEvaluationReminders() {
        LocalDate today = LocalDate.now(ZoneId.of(timezone));
        Set<AssetStatus> excludedStatuses = EnumSet.of(AssetStatus.IN_STOCK, AssetStatus.RETIRED);

        List<Asset> assets = assetRepository.findByStatusNotInAndPurchaseDateIsNotNull(excludedStatuses);
        int notificationCount = 0;

        for (Asset asset : assets) {
            if (asset.getAssignedTo() == null) {
                continue;
            }

            LocalDate milestone = nextEvaluationMilestone(asset, today);
            if (milestone == null) {
                continue;
            }

            long daysUntilMilestone = ChronoUnit.DAYS.between(today, milestone);
            if (daysUntilMilestone != leadDays) {
                continue;
            }

            if (markerRepository.existsByAsset_IdAndReminderMilestone(asset.getId(), milestone)) {
                continue;
            }

            List<User> recipients = resolveRecipients(asset);
            if (recipients.isEmpty()) {
                continue;
            }

            markerRepository.save(NotificationMarker.builder()
                    .asset(asset)
                    .reminderMilestone(milestone)
                    .build());

            String link = "/assets/" + asset.getId();
            String message = buildReminderMessage(asset, milestone);
            String title = "Tai san sap den han danh gia";

            List<NotificationCommand> commands = new ArrayList<>();
            for (User user : recipients) {
                commands.add(NotificationCommand.builder()
                        .userId(user.getId())
                        .title(title)
                        .message(message)
                        .linkUrl(link)
                        .assetId(asset.getId())
                        .type(NotificationType.REMINDER)
                        .build());
            }
            notificationService.dispatchBulk(commands);
            notificationCount += commands.size();
        }

        log.info("Evaluation reminder job completed. Notifications dispatched: {}", notificationCount);
        return notificationCount;
    }

    private List<User> resolveRecipients(Asset asset) {
        User holder = asset.getAssignedTo();
        if (holder == null || holder.getDepartment() == null) {
            return List.of();
        }

        Long departmentId = holder.getDepartment().getId();
        List<User> managers = new ArrayList<>(userRepository.findByDepartment_IdAndRole(departmentId, Role.MANAGER));

        if (managers.isEmpty() && holder.getDepartment().getManagerId() != null) {
            userRepository.findById(holder.getDepartment().getManagerId()).ifPresent(manager -> {
                boolean exists = managers.stream().anyMatch(u -> u.getId().equals(manager.getId()));
                if (!exists) {
                    managers.add(manager);
                }
            });
        }

        if (managers.isEmpty()) {
            managers.addAll(userRepository.findByRole(Role.ADMIN));
        }

        return managers;
    }

    private String buildReminderMessage(Asset asset, LocalDate milestone) {
        User holder = asset.getAssignedTo();
        String holderName = holder != null ? holder.getName() : "chua xac dinh";
        String formattedDate = milestone.toString();
        return "Tai san %s (Ma: %s) do %s dang giu, se den han danh gia vao ngay %s. Vui long thuc hien danh gia."
                .formatted(asset.getName(), asset.getCode(), holderName, formattedDate);
    }

    private LocalDate nextEvaluationMilestone(Asset asset, LocalDate today) {
        if (asset.getPurchaseDate() == null) {
            return null;
        }
        LocalDate purchaseDate = asset.getPurchaseDate().toLocalDate();
        LocalDate milestone = purchaseDate.plusYears(cycleYears);
        while (milestone.isBefore(today)) {
            milestone = milestone.plusYears(cycleYears);
        }
        return milestone;
    }
}
