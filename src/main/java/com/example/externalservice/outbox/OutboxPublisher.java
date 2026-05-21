package com.example.externalservice.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishNewEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW);

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        event.getTopic(),
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).get();

                event.setStatus(OutboxEventStatus.PUBLISHED);
                event.setPublishedAt(OffsetDateTime.now());
                event.setLastError(null);

                log.info("Outbox event {} published to topic {}",
                        event.getEventId(), event.getTopic());
            } catch (Exception exception) {
                event.setAttempts(event.getAttempts() + 1);
                event.setLastError(exception.getMessage());

                if (event.getAttempts() >= 5) {
                    event.setStatus(OutboxEventStatus.FAILED);
                }

                log.error("Failed to publish outbox event {}", event.getEventId(), exception);
            }
        }
    }
}