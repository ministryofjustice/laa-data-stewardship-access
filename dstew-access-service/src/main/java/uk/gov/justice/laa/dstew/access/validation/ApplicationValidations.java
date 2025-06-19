package uk.gov.justice.laa.dstew.access.validation;

import static uk.gov.justice.laa.dstew.access.validation.ValidationUtils.notNull;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.dstew.access.entity.ApplicationEntity;
import uk.gov.justice.laa.dstew.access.model.ApplicationRequestBody;
import uk.gov.justice.laa.dstew.access.model.ApplicationUpdateRequestBody;
import uk.gov.justice.laa.dstew.access.shared.security.EffectiveAuthorizationProvider;

/**
 * Validations for a particular domain (Applications).
 * Currently, this avoids any kind of validation framework apart from a holder for validation errors (ValidationState)
 * and an exception that wraps those errors (ValidationException).
 */
@Component
public class ApplicationValidations {
  private final EffectiveAuthorizationProvider entra;

  ApplicationValidations(EffectiveAuthorizationProvider entra) {
    this.entra = entra;
  }

  /**
   * Validate an ApplicationRequestBody instance.
   *
   * @param body DTO to validate.
   */
  public void checkApplicationRequestBody(final ApplicationRequestBody body) {
    final var state = ValidationErrors.empty();
    state.addIf(body.getProviderOfficeId() == null
            && !(notNull(body.getIsEmergencyApplication())
            && "NEW".equals(body.getStatusCode())),
        "BRR-01: Provider office id is required (unless unsubmitted ECT)");
    state.throwIfAny();
  }

  /**
   * Validated an ApplicationUpdateRequestBody instance.
   *
   * @param body    DTO to validate.
   * @param current existing persisted entity.
   */
  public void checkApplicationUpdateRequestBody(final ApplicationUpdateRequestBody body,
                                                final ApplicationEntity current) {
    ValidationErrors.empty()
        .addIf(body.getClientId() != null
                && entra.hasAppRole("Provider")
                && !entra.hasAnyAppRole("Caseworker", "Administrator"),
            "BRR-03: Provider role cannot update the client date of birth or NI number")
        .throwIfAny();
  }
}
