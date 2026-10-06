package com.help_bridge.audit_logging_spring_boot_starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "helpbridge.audit")
public record AuditLoggerProperties(
        boolean enabled,
        Long slowThresholdMs,
        @DefaultValue Include include
) {
    public record Include(
            boolean clientIp,
            boolean userAgent,
            boolean handlerMethod,
            boolean queryParams,
            boolean headers,
            boolean threadName
    ) {}
}
