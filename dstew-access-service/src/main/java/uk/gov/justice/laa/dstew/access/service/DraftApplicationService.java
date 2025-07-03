package uk.gov.justice.laa.dstew.access.service;

import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.dstew.access.mapper.DraftApplicationMapper;
import uk.gov.justice.laa.dstew.access.model.DraftApplicationCreateReq;
import uk.gov.justice.laa.dstew.access.repository.DraftApplicationRepository;
import uk.gov.justice.laa.dstew.access.validation.DraftApplicationValidations;

/**
 * Service class for handling draft items requests.
 */
@Service
public class DraftApplicationService {

  private final DraftApplicationRepository draftApplicationRepository;
  private final DraftApplicationValidations applicationValidations;
  private final DraftApplicationMapper applicationMapper;

  /**
   * Create a service for applications for legal aid.
   *
   * @param draftApplicationMapper JSON mapper to serialize the draft application.
   * @param draftApplicationRepository Manages reading and writing data to database.
   * @param applicationValidator the validation methods for request DTO.
   */
  public DraftApplicationService(
          final DraftApplicationRepository draftApplicationRepository,
          final DraftApplicationMapper draftApplicationMapper,
          final DraftApplicationValidations applicationValidator) {
    this.draftApplicationRepository = draftApplicationRepository;
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

    var savedEntity = draftApplicationRepository.save(applicationEntity);

    return UUID.randomUUID(); //savedEntity.getId();

  }
}
