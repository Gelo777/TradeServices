package com.example.telemetryhub;

import com.example.telemetryhub.mode.dto.TelemetryMessage;
import com.example.telemetryhub.service.TelemetryGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class TelemetryGeneratorTest {

    @Mock
    private KafkaTemplate<String, TelemetryMessage> kafkaTemplate;

    @InjectMocks
    private TelemetryGenerator telemetryGenerator;

    @Test
    void generateTelemetry() {
        Mockito.when(kafkaTemplate.send(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);

        telemetryGenerator.generateTelemetry();

        Mockito.verify(kafkaTemplate, Mockito.times(telemetryGenerator.getMessagesPerMinute() * telemetryGenerator.getTotalAgents()))
            .send(Mockito.any(), Mockito.any(), Mockito.any());
    }
}