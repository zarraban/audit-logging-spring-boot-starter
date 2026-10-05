package com.help_bridge.audit_logging_spring_boot_starter;

import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import com.help_bridge.audit_logging_spring_boot_starter.properties.LogAttributeContributor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AuditLoggerFilter implements HandlerInterceptor {

    private final List<LogAttributeContributor> contributors;
    private final AuditLoggerProperties properties;
    private final String startTimeAttribute;

    public AuditLoggerFilter(AuditLoggerProperties properties,
                             String startTimeAttribute,
                             List<LogAttributeContributor> contributors) {
        this.properties = properties;
        this.contributors = contributors;
        this.startTimeAttribute = startTimeAttribute;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(startTimeAttribute, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        Map<String, Object> context = new LinkedHashMap<>();

        if (request.getAttribute(startTimeAttribute) instanceof Long startTime) {
            Long timeForTheResponse = System.currentTimeMillis() - startTime;
            context.put("responseTime", timeForTheResponse + " ms");

            if (timeForTheResponse > properties.slowThresholdMs()) {
                log.warn("[AUDIT-SLOW] Response time is slower than threshold set [threshold = {}, actual = {}]", properties.slowThresholdMs(), timeForTheResponse);
            }
            request.removeAttribute(startTimeAttribute);
        }

        if (ex != null) {
            context.put("error", ex.getClass().getSimpleName());
            context.put("errorMessage", ex.getMessage());
        }

        context.put("method", request.getMethod());
        context.put("uri", request.getRequestURI());
        context.put("status", response.getStatus());

        for (LogAttributeContributor contributor : contributors) {
            contributor.contribute(request, response, handler, context);
        }

        log.info("[AUDIT] Audit results: {}", context);
    }
}

