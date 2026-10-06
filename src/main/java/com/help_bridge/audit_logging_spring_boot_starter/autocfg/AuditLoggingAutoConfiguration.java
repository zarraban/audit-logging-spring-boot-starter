package com.help_bridge.audit_logging_spring_boot_starter.autocfg;

import com.help_bridge.audit_logging_spring_boot_starter.AuditLoggerFilter;
import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import com.help_bridge.audit_logging_spring_boot_starter.properties.LogAttributeContributor;
import com.help_bridge.audit_logging_spring_boot_starter.properties.impl.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Stream;


@AutoConfiguration
@ConditionalOnClass(OncePerRequestFilter.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "helpbridge.audit", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(AuditLoggerProperties.class)
public class AuditLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditLoggerFilter auditLoggerFilter(AuditLoggerProperties properties) {
        String startTimeAttribute = AuditLoggerFilter.class.getName() + ".START_TIME";

        List<LogAttributeContributor> logAttributeContributors = Stream.of(
                new ClientIpContributor(properties),
                new HandlerMethodContributor(properties),
                new HeadersContributor(properties),
                new QueryParamsContributor(properties),
                new ThreadNameContributor(properties),
                new UserAgentContributor(properties)
        ).filter(LogAttributeContributor::isEnabled).toList();

        return new AuditLoggerFilter(properties,
                startTimeAttribute,
                logAttributeContributors
        );
    }

    @Bean
    public WebMvcConfigurer auditLoggerMvcConfigurer(AuditLoggerFilter auditLoggerFilter) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(auditLoggerFilter);
            }
        };
    }
}
