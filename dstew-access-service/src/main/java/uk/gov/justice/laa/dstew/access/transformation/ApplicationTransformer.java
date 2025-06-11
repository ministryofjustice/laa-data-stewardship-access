package uk.gov.justice.laa.dstew.access.transformation;

import static uk.gov.justice.laa.dstew.access.shared.security.SecurityUtils.hasAuthority;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.dstew.access.model.Application;

/**
 * Provide role-based transformation to the Application responses.
 */
@Component
class ApplicationTransformer implements ResponseTransformer<Application> {
  @Override
  public Application transform(final Application response, final Collection<? extends GrantedAuthority> authorities) {
    if (hasAuthority(authorities, "APPROLE_RedactProceedings")) {
      response.setProceedings(null);
    }
    return response;
  }
}
