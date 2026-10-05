package com.help_bridge.audit_logging_spring_boot_starter.properties.impl;

import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import com.help_bridge.audit_logging_spring_boot_starter.properties.LogAttributeContributor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class HeadersContributor implements LogAttributeContributor {
    private final AuditLoggerProperties properties;

    @Override
    public boolean isEnabled() {
        return properties.include().headers();
    }

    @Override
    public void contribute(HttpServletRequest request, HttpServletResponse response, Object handler, Map<String, Object> contributionContext) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return;
        }

        Map<String, Object> headersMap = new LinkedHashMap<>();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> values = Collections.list(request.getHeaders(headerName));
            headersMap.put(headerName, values.size() == 1 ? values.get(0) : values);
        }

        contributionContext.put("headers", headersMap);
    }
}
