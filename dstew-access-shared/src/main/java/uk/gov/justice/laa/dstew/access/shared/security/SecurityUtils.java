package uk.gov.justice.laa.dstew.access.shared.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security utility methods.
 */
public final class SecurityUtils {
  private SecurityUtils() {
    // prevent instantiation.
  }

  /**
   * Check if a collection of GrantedAuthority instances contains a given name.
   *
   * @param authorities Collection of GrantedAuthority.
   * @param name String authority name to look for.
   * @return boolean true if it was found.
   */
  public static boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String name) {
    return authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(name::equals);
  }

  /**
   * Check if a collection of GrantedAuthority instances contains any of the given names.
   *
   * @param authorities Collection of GrantedAuthority.
   * @param names String authority names to look for.
   * @return boolean true if any was found.
   */
  public static boolean hasAnyAuthority(Collection<? extends GrantedAuthority> authorities, String... names) {
    final var nameSet = Set.of(names);
    return authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(nameSet::contains);
  }

  /**
   * Get the collection of GrantedAuthority for current Principal.
   *
   * @return Collection of GrantedAuthority.
   */
  public static Collection<? extends GrantedAuthority> getAuthorities() {
    return Optional.of(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(Authentication::getAuthorities)
        .orElse(List.of());
  }
}
