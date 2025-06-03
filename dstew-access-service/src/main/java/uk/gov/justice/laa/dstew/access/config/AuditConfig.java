package uk.gov.justice.laa.dstew.access.config;

import java.time.Instant;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuration for JPA auditing (automatic created & lastModified metadata).
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "dateTimeProvider")
class AuditConfig {

  /**
   * Auditor provider implementation for the application.
   *
   * @return provider of current user.
   */
  @Bean("auditorProvider")
  AuditorAware<String> auditorProvider() {
    return () -> Optional.of(SecurityContextHolder.getContext())
              .map(SecurityContext::getAuthentication)
              .filter(Authentication::isAuthenticated)
              .map(Authentication::getName);
  }

  /**
   * Used by the Spring Data date auditing annotations.
   *
   * @return provider of current time.
   */
  @Bean("dateTimeProvider")
  DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(Instant.now());
  }
}
