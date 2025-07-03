package uk.gov.justice.laa.dstew.access.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.dstew.access.model.DraftApplicationCreateReq;

/**
 * Validations for a draft application.
 * Validates that at least one of the 3 initial fields are present
 */
@Component
@RequiredArgsConstructor
public class DraftApplicationValidations {
  /**
   * Validate an ApplicationV1CreateReq instance.
   *
   * @param dto DTO to validate.
   * */
  public void checkCreateRequest(final DraftApplicationCreateReq dto) {
    final var state = ValidationErrors.empty();
    state.addIf(dto.getClientId() == null
                    && dto.getProviderId() == null,
            "Insufficient data provided for draft application creation");
    state.throwIfAny();
  }
}
