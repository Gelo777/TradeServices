package com.example.telemetryhub.mode.dto;

import lombok.Data;

@Data
public class TelemetryMessage {
    private String uuid;
    private String agentId;
    private long previousMessageTime;
    private String activeService;
    private int qualityScore;
}
