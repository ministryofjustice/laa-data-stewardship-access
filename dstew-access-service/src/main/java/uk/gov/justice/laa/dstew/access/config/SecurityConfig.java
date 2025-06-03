package uk.gov.justice.laa.dstew.access.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration if security is enabled.
 */
@ConditionalOnProperty(name = "spring.cloud.azure.active-directory.enabled", havingValue = "true")
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
  /**
   * Return the security filter chain.
   *
   * @param http Used to configure web security.
   * @return The built security configuration.
   * @throws Exception if anything went wrong.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .with(AadResourceServerHttpSecurityConfigurer.aadResourceServer(),
                    withDefaults())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(withDefaults()));
    return http.build();
  }
}

