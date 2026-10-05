package com.help_bridge.audit_logging_spring_boot_starter.properties.impl;

import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import com.help_bridge.audit_logging_spring_boot_starter.properties.LogAttributeContributor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;


public class ThreadNameContributor implements LogAttributeContributor {
    private final AuditLoggerProperties properties;

    public ThreadNameContributor(AuditLoggerProperties properties){
        this.properties = properties;
    }

    @Override
    public boolean isEnabled() {
        return properties.include().threadName();
    }

    @Override
    public void contribute(HttpServletRequest request, HttpServletResponse response, Object handler, Map<String, Object> contributionContext) {
        Thread thread = Thread.currentThread();

        contributionContext.put("threadName", thread.getName());
        contributionContext.put("threadId", thread.threadId());
        contributionContext.put("isVirtualThread", thread.isVirtual());
    }
}
