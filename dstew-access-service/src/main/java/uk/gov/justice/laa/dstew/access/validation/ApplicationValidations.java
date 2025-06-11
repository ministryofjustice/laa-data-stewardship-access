package uk.gov.justice.laa.dstew.access.validation;

import static uk.gov.justice.laa.dstew.access.shared.security.SecurityUtils.getAuthorities;
import static uk.gov.justice.laa.dstew.access.shared.security.SecurityUtils.hasAnyAuthority;
import static uk.gov.justice.laa.dstew.access.shared.security.SecurityUtils.hasAuthority;
import static uk.gov.justice.laa.dstew.access.validation.ValidationUtils.notNull;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.dstew.access.entity.ApplicationEntity;
import uk.gov.justice.laa.dstew.access.model.ApplicationRequestBody;
import uk.gov.justice.laa.dstew.access.model.ApplicationUpdateRequestBody;

/**
 * Validations for a particular domain (Applications).
 * Currently, this avoids any kind of validation framework apart from a holder for validation errors (ValidationState)
 * and an exception that wraps those errors (ValidationException).
 */
@Component
public class ApplicationValidations {

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
    var authorities = getAuthorities();
    ValidationErrors.empty()
        .addIf(body.getClientId() != null
                && hasAuthority(authorities, "APPROLE_Provider")
                && !hasAnyAuthority(authorities, "APPROLE_Caseworker", "APPROLE_Administrator"),
            "BRR-03: Provider role cannot update the client date of birth or NI number")
        .throwIfAny();
  }
}
