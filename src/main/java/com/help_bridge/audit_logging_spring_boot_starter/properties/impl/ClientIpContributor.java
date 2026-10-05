package com.help_bridge.audit_logging_spring_boot_starter.properties.impl;

import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import com.help_bridge.audit_logging_spring_boot_starter.properties.LogAttributeContributor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ClientIpContributor implements LogAttributeContributor {
    private final AuditLoggerProperties properties;

    @Override
    public boolean isEnabled() {
        return properties.include().clientIp();
    }

    @Override
    public void contribute(HttpServletRequest request, HttpServletResponse response, Object handler, Map<String, Object> contributionContext) {
        String clientIp = request.getRemoteAddr();

        contributionContext.put("clientIp", clientIp != null && !clientIp.isBlank() ?
                clientIp : "unknown");
    }
}
