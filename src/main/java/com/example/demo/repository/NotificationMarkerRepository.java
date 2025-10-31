package com.example.demo.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.NotificationMarker;

public interface NotificationMarkerRepository extends JpaRepository<NotificationMarker, Long> {

    boolean existsByAsset_IdAndReminderMilestone(Long assetId, LocalDate reminderMilestone);

    Optional<NotificationMarker> findByAsset_IdAndReminderMilestone(Long assetId, LocalDate reminderMilestone);
}
