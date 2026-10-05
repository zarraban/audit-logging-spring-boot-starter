package com.help_bridge.audit_logging_spring_boot_starter.properties.impl;

import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import com.help_bridge.audit_logging_spring_boot_starter.properties.LogAttributeContributor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;

@RequiredArgsConstructor
public class HandlerMethodContributor implements LogAttributeContributor {
    private final AuditLoggerProperties properties;

    @Override
    public boolean isEnabled() {
        return properties.include().handlerMethod();
    }

    @Override
    public void contribute(HttpServletRequest request, HttpServletResponse response, Object handler, Map<String, Object> contributionContext) {
        if(handler instanceof HandlerMethod hm){
            contributionContext.put("handlerClass", hm.getBeanType().getSimpleName());
            contributionContext.put("handlerMethod", hm.getMethod().getName());
        }
    }
}
