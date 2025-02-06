package cc.xiaonuo.flow.model;

import lombok.Data;

@Data
public class AlertConfig {
    private long maxDuration;
    private String alertType;
    private String alertTarget;
} 