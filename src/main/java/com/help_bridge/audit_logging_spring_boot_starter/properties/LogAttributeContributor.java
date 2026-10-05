package com.help_bridge.audit_logging_spring_boot_starter.properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface LogAttributeContributor {
    boolean isEnabled();

    void contribute(HttpServletRequest request, HttpServletResponse response, Object handler,
                    Map<String, Object> contributionContext);
}
