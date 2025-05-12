package uk.gov.justice.laa.access.datastore.service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.access.datastore.config.SqsProducer;
import uk.gov.justice.laa.access.datastore.entity.ApplicationEntity;
import uk.gov.justice.laa.access.datastore.entity.ApplicationHistoryEntity;
import uk.gov.justice.laa.access.datastore.exception.ApplicationNotFoundException;
import uk.gov.justice.laa.access.datastore.mapper.ApplicationMapper;
import uk.gov.justice.laa.access.datastore.model.Application;
import uk.gov.justice.laa.access.datastore.model.ApplicationHistoryMessage;
import uk.gov.justice.laa.access.datastore.model.ApplicationHistoryRequestBody;
import uk.gov.justice.laa.access.datastore.model.ApplicationRequestBody;
import uk.gov.justice.laa.access.datastore.repository.ApplicationHistoryRepository;
import uk.gov.justice.laa.access.datastore.repository.ApplicationRepository;

/**
 * Service class for handling items requests.
 */
@RequiredArgsConstructor
@Service
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  private final ApplicationHistoryRepository applicationHistoryRepository;

  private final ApplicationMapper applicationMapper;

  private final SqsProducer sqsProducer;

  /**
   * Gets all applications.
   *
   * @return the list of applications
   */
  public List<Application> getAllApplications() {
    return applicationRepository.findAll().stream().map(applicationMapper::toApplication).toList();
  }

  /**
   * Gets an application for a given id.
   *
   * @param id the application id
   * @return the requested application
   */
  public Application getApplication(UUID id) {
    ApplicationEntity applicationEntity = checkIfApplicationExists(id);
    return applicationMapper.toApplication(applicationEntity);
  }

  /**
   * Creates an application.
   *
   * @param applicationRequestBody the application to be created
   * @return the id of the created application
   */
  public UUID createApplication(ApplicationRequestBody applicationRequestBody) {
    ApplicationEntity applicationEntity =
        applicationMapper.toApplicationEntity(applicationRequestBody);

    //set the application entity id to null to ensure a new entity is created
    if (applicationEntity.getProceedings() != null){
      applicationEntity.getProceedings().forEach(proceeding -> {
        proceeding.setApplication(applicationEntity);
      });
    }

    applicationRepository.save(applicationEntity);

    //create history message for the created application
    ApplicationHistoryMessage historyMessage = ApplicationHistoryMessage.builder()
        .userId(applicationEntity.getUpdatedBy())
        .action(ApplicationHistoryMessage.ActionEnum.CREATED)
        .resourceType(ApplicationHistoryMessage.ResourceTypeEnum.APPLICATION)
        .resourceId(applicationEntity.getId())
        .timestamp(applicationEntity.getCreatedAt().atOffset(ZoneOffset.UTC))
        .build();
    sqsProducer.createHistoricRecord(historyMessage);

    return applicationEntity.getId();
  }

  public UUID createApplicationHistory(UUID applicationId, ApplicationHistoryRequestBody applicationHistoryRequestBody) {
    ApplicationEntity applicationEntity = checkIfApplicationExists(applicationId);

    //todo convert to mapper method
    ApplicationHistoryEntity applicationHistoryEntity =
        new ApplicationHistoryEntity();

    applicationHistoryEntity.setApplicationId(applicationId);
    applicationHistoryEntity.setUserId(applicationHistoryRequestBody.getUserId());
    applicationHistoryEntity.setAction(applicationHistoryRequestBody.getAction());
    applicationHistoryEntity.setResourceType(applicationHistoryRequestBody.getResourceType());
    applicationHistoryEntity.setApplicationSnapshot(applicationHistoryRequestBody.getApplicationSnapshot());
    applicationHistoryEntity.setTimestamp(applicationHistoryRequestBody.getTimestamp().toInstant());

    applicationHistoryRepository.save(applicationHistoryEntity);
    return applicationEntity.getId();
  }

  protected ApplicationEntity checkIfApplicationExists(UUID id) {
    return applicationRepository
        .findById(id)
        .orElseThrow(
            () -> new ApplicationNotFoundException(String.format("No application found with id: %s", id)));
  }
}
