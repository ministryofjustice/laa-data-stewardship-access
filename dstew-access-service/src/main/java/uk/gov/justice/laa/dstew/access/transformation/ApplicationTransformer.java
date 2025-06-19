package uk.gov.justice.laa.dstew.access.transformation;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.dstew.access.model.Application;
import uk.gov.justice.laa.dstew.access.shared.security.EffectiveAuthorizationProvider;

/**
 * Provide role-based transformation to the Application responses.
 */
@Component
class ApplicationTransformer implements ResponseTransformer<Application> {
  private final EffectiveAuthorizationProvider entra;

  ApplicationTransformer(final EffectiveAuthorizationProvider entra) {
    this.entra = entra;
  }

  @Override
  public Application transform(final Application response) {
    if (entra.hasAppRole("RedactProceedings")) {
      response.setProceedings(null);
    }
    return response;
  }
}
