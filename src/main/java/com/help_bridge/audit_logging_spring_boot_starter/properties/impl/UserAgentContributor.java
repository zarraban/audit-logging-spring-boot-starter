package com.help_bridge.audit_logging_spring_boot_starter.properties.impl;

import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import com.help_bridge.audit_logging_spring_boot_starter.properties.LogAttributeContributor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@RequiredArgsConstructor
public class UserAgentContributor implements LogAttributeContributor {
    private final AuditLoggerProperties properties;

    @Override
    public boolean isEnabled() {
        return properties.include().userAgent();
    }

    @Override
    public void contribute(HttpServletRequest request, HttpServletResponse response, Object handler, Map<String, Object> contributionContext) {
        String userAgentHeader = request.getHeader(HttpHeaders.USER_AGENT);
        contributionContext.put("userAgent", userAgentHeader != null && !userAgentHeader.isBlank() ?
                userAgentHeader : "unknown");
    }
}
