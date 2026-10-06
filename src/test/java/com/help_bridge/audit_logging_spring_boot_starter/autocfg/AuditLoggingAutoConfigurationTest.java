package com.help_bridge.audit_logging_spring_boot_starter.autocfg;

import com.help_bridge.audit_logging_spring_boot_starter.AuditLoggerFilter;
import com.help_bridge.audit_logging_spring_boot_starter.properties.AuditLoggerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.assertj.core.api.Assertions.assertThat;

class AuditLoggingAutoConfigurationTest {

    private final WebApplicationContextRunner webRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AuditLoggingAutoConfiguration.class));

    @Test
    void shouldRegisterFilterWhenEnabled() {
        webRunner
                .withPropertyValues("helpbridge.audit.enabled=true")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(AuditLoggerFilter.class);
                    assertThat(context).hasSingleBean(AuditLoggerProperties.class);
                    // MvcConfigurer must be present so Spring MVC actually invokes the interceptor
                    assertThat(context).hasBean("auditLoggerMvcConfigurer");
                });
    }

    @Test
    void shouldNotRegisterFilterWhenPropertyMissing() {
        webRunner.run(context -> {
            assertThat(context).doesNotHaveBean(AuditLoggerFilter.class);
            assertThat(context).doesNotHaveBean(AuditLoggerProperties.class);
            assertThat(context).doesNotHaveBean("auditLoggerMvcConfigurer");
        });
    }

    @Test
    void shouldNotRegisterFilterWhenDisabled() {
        webRunner
                .withPropertyValues("helpbridge.audit.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(AuditLoggerFilter.class);
                    assertThat(context).doesNotHaveBean("auditLoggerMvcConfigurer");
                });
    }

    @Test
    void shouldNotRegisterFilterInNonWebApplication() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(AuditLoggingAutoConfiguration.class))
                .withPropertyValues("helpbridge.audit.enabled=true")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(AuditLoggerFilter.class);
                    assertThat(context).doesNotHaveBean("auditLoggerMvcConfigurer");
                });
    }

    @Test
    void shouldNotRegisterFilterWhenServletClassMissing() {
        webRunner
                .withClassLoader(new FilteredClassLoader(OncePerRequestFilter.class))
                .withPropertyValues("helpbridge.audit.enabled=true")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(AuditLoggerFilter.class);
                    assertThat(context).doesNotHaveBean("auditLoggerMvcConfigurer");
                });
    }

    @Test
    void shouldBindIncludeFlagsAndThreshold() {
        webRunner
                .withPropertyValues(
                        "helpbridge.audit.enabled=true",
                        "helpbridge.audit.slow-threshold-ms=500",
                        "helpbridge.audit.include.client-ip=true",
                        "helpbridge.audit.include.headers=true")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    AuditLoggerProperties props = context.getBean(AuditLoggerProperties.class);
                    assertThat(props.slowThresholdMs()).isEqualTo(500L);
                    assertThat(props.include().clientIp()).isTrue();
                    assertThat(props.include().headers()).isTrue();
                    assertThat(props.include().userAgent()).isFalse();
                });
    }

    @Test
    void shouldBackOffWhenUserDefinesOwnFilter() {
        webRunner
                .withPropertyValues("helpbridge.audit.enabled=true")
                .withBean("customAuditLoggerFilter", AuditLoggerFilter.class,
                        () -> org.mockito.Mockito.mock(AuditLoggerFilter.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    // only the user's bean — auto-configured one backed off
                    assertThat(context).hasSingleBean(AuditLoggerFilter.class);
                    assertThat(context).hasBean("customAuditLoggerFilter");
                    assertThat(context).doesNotHaveBean("auditLoggerFilter");
                    // MvcConfigurer still present and will use the user's custom bean
                    assertThat(context).hasBean("auditLoggerMvcConfigurer");
                });
    }
}
