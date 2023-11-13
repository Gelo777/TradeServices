package com.example.telemetryhub.service;

import com.example.telemetryhub.mode.dto.TelemetryMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Getter
public class TelemetryGenerator {

    private final KafkaTemplate<String, TelemetryMessage> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String kafkaTopic;

    @Value("${telemetry.agents.total}")
    private int totalAgents;

    @Value("${telemetry.messages.per-minute}")
    private int messagesPerMinute;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void generateTelemetryScheduled() {
        generateTelemetry();
    }

    public void generateTelemetry() {
        for (int agentNumber = 1; agentNumber <= totalAgents; agentNumber++) {
            for (int i = 0; i < messagesPerMinute; i++) {
                TelemetryMessage telemetryMessage = generateRandomTelemetry(agentNumber);
                kafkaTemplate.send(kafkaTopic, telemetryMessage.getAgentId(), telemetryMessage);
            }
        }
    }

    private TelemetryMessage generateRandomTelemetry(int agentNumber) {
        TelemetryMessage telemetryMessage = new TelemetryMessage();
        telemetryMessage.setUuid(UUID.randomUUID().toString());
        telemetryMessage.setAgentId("agent-" + agentNumber);
        telemetryMessage.setPreviousMessageTime(Instant.now().minusSeconds(604800).toEpochMilli());
        telemetryMessage.setActiveService("netflix");
        telemetryMessage.setQualityScore(new Random().nextInt(100) + 1);
        return telemetryMessage;
    }
}
