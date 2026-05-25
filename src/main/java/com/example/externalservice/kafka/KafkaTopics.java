package com.example.externalservice.kafka;

public final class KafkaTopics {

    public static final String STUDENT_CREATE_REQUESTS = "student-create-requests";
    public static final String DLQ_EVENTS = "student-events-dlq";

    private KafkaTopics() {
    }
}