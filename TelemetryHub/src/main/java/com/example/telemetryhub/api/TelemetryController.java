package com.example.telemetryhub.api;

import com.example.telemetryhub.mode.dto.TelemetryMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final KafkaTemplate<String, TelemetryMessage> kafkaTemplate;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Value("${telemetry.agents.total}")
    private int totalAgents;

    @Value("${telemetry.messages.per-minute}")
    private int messagesPerMinute;

    public TelemetryController(KafkaTemplate<String, TelemetryMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    @PostMapping("/generate")
    public Mono<ResponseEntity<String>> generateTelemetry() {
        return Mono.fromCallable(() -> {
            for (int agentNumber = 1; agentNumber <= totalAgents; agentNumber++) {
                for (int i = 0; i < messagesPerMinute; i++) {
                    TelemetryMessage telemetryMessage = generateRandomTelemetry(agentNumber);
                    kafkaTemplate.send(kafkaTopic, telemetryMessage.getAgentId(), telemetryMessage);
                }
            }
            return "Telemetry messages generated and sent successfully";
        }).map(response -> new ResponseEntity<>(response, HttpStatus.OK));
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
