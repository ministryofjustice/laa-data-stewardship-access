package uk.gov.justice.laa.dstew.access.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration if security is enabled.
 */
@ConditionalOnProperty(name = "feature.security", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
class SecurityConfig {
  /**
   * Return the security filter chain.
   *
   * @param http Used to configure web security.
   * @return The built security configuration.
   * @throws Exception if anything went wrong.
   */
  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .anyRequest().authenticated())
        .with(AadResourceServerHttpSecurityConfigurer.aadResourceServer(), withDefaults())
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }
}
