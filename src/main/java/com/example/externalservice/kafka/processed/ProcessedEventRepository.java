package com.example.externalservice.kafka.processed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventId(UUID eventId);

    Optional<ProcessedEvent> findByEventId(UUID eventId);
}
