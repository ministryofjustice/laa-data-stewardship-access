package uk.gov.justice.laa.access.datastore.config.audit;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

/**
 * Auditor provider implementation for the application. This class is responsible for storing the
 * current user in a thread local variable, and returning it when requested.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    String currentUser = "test-user";
    return Optional.of(currentUser);
  }

}

