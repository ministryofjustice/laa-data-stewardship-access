package uk.gov.justice.laa.dstew.access.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.dstew.access.mapper.DraftApplicationMapper;
import uk.gov.justice.laa.dstew.access.model.DraftApplicationCreateReq;
import uk.gov.justice.laa.dstew.access.validation.DraftApplicationValidations;

/**
 * Service class for handling draft items requests.
 */
@Service
public class DraftApplicationService {

  private final DraftApplicationValidations applicationValidations;
  private final DraftApplicationMapper applicationMapper;
  private final ObjectMapper objectMapper;

  /**
   * Create a service for applications for legal aid.
   *
   * @param objectMapper JSON mapper to serialize the history.
   * @param draftApplicationMapper JSON mapper to serialize the history.
   * @param applicationValidator the validation methods for request DTO.
   */
  public DraftApplicationService(
          final ObjectMapper objectMapper,
          final DraftApplicationMapper draftApplicationMapper,
          final DraftApplicationValidations applicationValidator) {
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    this.objectMapper = objectMapper;
    this.applicationMapper = draftApplicationMapper;
    this.applicationValidations = applicationValidator;
  }

  /**
   * Creates an application.
   *
   * @param applicationCreateReq the application to be created
   * @return the id of the created application
  */
  @PreAuthorize("@entra.hasAppRole('ApplicationWriter')")
  public UUID createApplication(DraftApplicationCreateReq applicationCreateReq) {
    applicationValidations.checkCreateRequest(applicationCreateReq);
    var applicationEntity = applicationMapper.toDraftApplicationEntity(applicationCreateReq);
    /*
    var savedEntity = applicationRepository.save(applicationEntity);

    return savedEntity.getId();
    */
    return UUID.randomUUID();
  }
}
