package uk.gov.justice.laa.dstew.access.transformation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.dstew.access.model.ApplicationV1;
import uk.gov.justice.laa.dstew.access.shared.security.EffectiveAuthorizationProvider;

/**
 * Provide role-based transformation to the Application responses.
 */
@Component
@RequiredArgsConstructor
class ApplicationV1Transformer implements ResponseTransformer<ApplicationV1> {
  private final EffectiveAuthorizationProvider entra;

  @Override
  public ApplicationV1 transform(final ApplicationV1 response) {
    if (!entra.hasAppRole("ProceedingReader")) {
      response.setProceedings(null);
    }
    return response;
  }
}
