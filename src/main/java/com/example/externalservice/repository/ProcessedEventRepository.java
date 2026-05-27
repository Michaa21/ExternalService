package com.example.externalservice.repository;

import com.example.externalservice.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    @Modifying
    @Query(value = """
            insert into processed_events (id, event_id, event_type, processed_at)
            values (:id, :eventId, :eventType, now())
            on conflict (event_id) do nothing
            """, nativeQuery = true)
    int insertIfNotExists(@Param("id") UUID id,
                          @Param("eventId") UUID eventId,
                          @Param("eventType") String eventType);
}
