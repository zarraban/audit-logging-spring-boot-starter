package com.help_bridge.audit_logging_spring_boot_starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "helpbridge.audit")
public record AuditLoggerProperties(
        boolean enabled,
        Long slowThresholdMs,
        Include include
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
