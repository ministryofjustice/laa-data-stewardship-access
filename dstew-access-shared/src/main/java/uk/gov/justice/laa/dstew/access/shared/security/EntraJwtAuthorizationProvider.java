package uk.gov.justice.laa.dstew.access.shared.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security authorization methods.
 */
@ConditionalOnProperty(name = "feature.security", havingValue = "true", matchIfMissing = true)
@Component("entra")
final class EntraJwtAuthorizationProvider implements EffectiveAuthorizationProvider {
  @Override
  public boolean hasAppRole(final String name) {
    return getAuthorities().contains("APPROLE_" + name);
  }

  @Override
  public boolean hasAnyAppRole(final String... names) {
    final var authorities = getAuthorities();
    return Arrays.stream(names)
        .anyMatch(name -> authorities.contains("APPROLE_" + name));
  }

  private Set<String> getAuthorities() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (auth != null && auth.isAuthenticated()) ? auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toUnmodifiableSet()) : Set.of();
  }
}
