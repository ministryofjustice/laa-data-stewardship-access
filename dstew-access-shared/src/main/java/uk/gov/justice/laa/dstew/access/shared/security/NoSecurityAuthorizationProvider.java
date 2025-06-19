package uk.gov.justice.laa.dstew.access.shared.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "feature.security", havingValue = "false")
@Component("entra")
final class NoSecurityAuthorizationProvider implements EffectiveAuthorizationProvider {
  @Override
  public boolean hasAppRole(final String name) {
    return true;
  }

  @Override
  public boolean hasAnyAppRole(final String... names) {
    return names.length > 0;
  }
}
