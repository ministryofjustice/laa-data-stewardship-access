package uk.gov.justice.laa.dstew.access.service;

import static uk.gov.justice.laa.dstew.access.model.ActionType.CREATED;
import static uk.gov.justice.laa.dstew.access.model.ActionType.UPDATED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.dstew.access.config.SqsProducer;
import uk.gov.justice.laa.dstew.access.entity.ApplicationEntity;
import uk.gov.justice.laa.dstew.access.entity.ApplicationHistoryEntity;
import uk.gov.justice.laa.dstew.access.exception.ApplicationNotFoundException;
import uk.gov.justice.laa.dstew.access.mapper.ApplicationMapper;
import uk.gov.justice.laa.dstew.access.model.ActionType;
import uk.gov.justice.laa.dstew.access.model.Application;
import uk.gov.justice.laa.dstew.access.model.ApplicationHistoryEntry;
import uk.gov.justice.laa.dstew.access.model.ApplicationHistoryMessage;
import uk.gov.justice.laa.dstew.access.model.ApplicationHistoryRequestBody;
import uk.gov.justice.laa.dstew.access.model.ApplicationProceeding;
import uk.gov.justice.laa.dstew.access.model.ApplicationRequestBody;
import uk.gov.justice.laa.dstew.access.model.ApplicationResourceType;
import uk.gov.justice.laa.dstew.access.model.ApplicationUpdateRequestBody;
import uk.gov.justice.laa.dstew.access.repository.ApplicationHistoryRepository;
import uk.gov.justice.laa.dstew.access.repository.ApplicationRepository;

/**
 * Service class for handling items requests.
 */
@Service
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  private final ApplicationHistoryRepository applicationHistoryRepository;

  private final ApplicationMapper applicationMapper;

  private final SqsProducer sqsProducer;

  private final ObjectMapper objectMapper;

  private final Javers javers;

  /**
   * Create a service for applications for legal aid.
   *
   * @param applicationRepository the repository of such applications.
   * @param applicationHistoryRepository the repository of the history of applications.
   * @param applicationMapper the mapper between entity and DTO.
   * @param sqsProducer the sender of messages to the queue.
   * @param objectMapper JSON mapper to serialize the history.
   */
  public ApplicationService(
      final ApplicationRepository applicationRepository,
      final ApplicationHistoryRepository applicationHistoryRepository,
      final ApplicationMapper applicationMapper,
      final SqsProducer sqsProducer,
      final ObjectMapper objectMapper) {
    this.applicationRepository = applicationRepository;
    this.applicationHistoryRepository = applicationHistoryRepository;
    this.applicationMapper = applicationMapper;
    this.sqsProducer = sqsProducer;
    this.javers = JaversBuilder.javers().build();

    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    this.objectMapper = objectMapper;
  }

  /**
   * Gets all applications.
   *
   * @return the list of applications
   */
  @PreAuthorize("hasAuthority('SCOPE_Application.Read') or hasAuthority('APPROLE_ApplicationReader')")
  public List<Application> getAllApplications() {
    return applicationRepository.findAll().stream().map(applicationMapper::toApplication).toList();
  }

  /**
   * Gets an application for a given id.
   *
   * @param id the application id
   * @return the requested application
   */
  @PreAuthorize("hasAuthority('SCOPE_Application.Read') or hasAuthority('APPROLE_ApplicationReader')")
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
  @PreAuthorize("hasAuthority('SCOPE_Application.Write') or hasAuthority('APPROLE_ApplicationWriter')")
  public UUID createApplication(ApplicationRequestBody applicationRequestBody) {
    ApplicationEntity applicationEntity =
        applicationMapper.toApplicationEntity(applicationRequestBody);

    //set the application entity id to null to ensure a new entity is created
    if (applicationEntity.getProceedings() != null) {
      applicationEntity.getProceedings().forEach(proceeding -> {
        proceeding.setApplication(applicationEntity);
      });
    }

    applicationRepository.save(applicationEntity);

    // create history message for the created application
    createAndSendHistoricRecord(applicationEntity, CREATED);

    return applicationEntity.getId();
  }

  /**
   * Update an application for legal aid, keeping history.
   *
   * @param id the unique identifier of the application.
   * @param requestBody the DTO containing the change.
   */
  @PreAuthorize("hasAuthority('SCOPE_Application.Write') or hasAuthority('APPROLE_ApplicationWriter')")
  public void updateApplication(UUID id, ApplicationUpdateRequestBody requestBody) {
    ApplicationEntity applicationEntity = checkIfApplicationExists(id);

    applicationMapper.updateApplicationEntity(applicationEntity, requestBody);
    if (applicationEntity.getProceedings() != null) {
      applicationEntity.getProceedings().forEach(p -> p.setApplication(applicationEntity));
    }

    applicationRepository.save(applicationEntity);

    Map<String, Object> snapshot =
        objectMapper.convertValue(applicationMapper.toApplication(applicationEntity), new TypeReference<>() {});

    ApplicationHistoryMessage message = ApplicationHistoryMessage.builder()
        .userId(applicationEntity.getUpdatedBy())
        .action(UPDATED)
        .resourceTypeChanged(ApplicationResourceType.APPLICATION)
        .applicationId(applicationEntity.getId())
        .historicSnapshot(snapshot)
        .timestamp(OffsetDateTime.now())
        .build();

    sqsProducer.createHistoricRecord(message);
  }


  /**
   * Gets all history for an application.
   *
   * @return the list of history for an application
   */
  @PreAuthorize("hasAuthority('SCOPE_Application.Read') or hasAuthority('APPROLE_ApplicationReader')")
  public List<ApplicationHistoryEntry> getAllApplicationHistory(UUID applicationId) {
    return applicationHistoryRepository.findByApplicationId(applicationId)
        .stream().map(applicationMapper::toApplicationHistoryEntry).toList();
  }

  protected void createAndSendHistoricRecord(ApplicationEntity applicationEntity, ActionType actionType) {
    Map<String, Object> historicSnapshot =
        objectMapper.convertValue(
            applicationMapper.toApplication(applicationEntity),
            new TypeReference<Map<String, Object>>() {});

    ApplicationHistoryMessage historyMessage = ApplicationHistoryMessage.builder()
        .userId(applicationEntity.getUpdatedBy())
        .action(ActionType.CREATED)
        .resourceTypeChanged(ApplicationResourceType.APPLICATION)
        .applicationId(applicationEntity.getId())
        .historicSnapshot(historicSnapshot)
        .timestamp(applicationEntity.getCreatedAt().atOffset(ZoneOffset.UTC))
        .build();

    sqsProducer.createHistoricRecord(historyMessage);
  }

  /**
   * Get latest history for an application.
   *
   * @param applicationId unique identifier of the application.
   * @return the latest history for the application.
   */
  @PreAuthorize("hasAuthority('SCOPE_Application.Read') or hasAuthority('APPROLE_ApplicationReader')")
  public ApplicationHistoryEntry getApplicationsLatestHistory(UUID applicationId) {
    checkIfApplicationExists(applicationId);

    ApplicationHistoryEntity latestEntry = applicationHistoryRepository
        .findFirstByApplicationIdOrderByTimestampDesc(applicationId)
        .orElseThrow(() ->
            new ApplicationNotFoundException("No history found for application id: " + applicationId));

    return applicationMapper.toApplicationHistoryEntry(latestEntry);
  }

  /**
   * Create a history record for an application.
   *
   * @param applicationId unique identifier of the application.
   * @param applicationHistoryRequestBody the DTO containing the history.
   * @return a unique identifier for the history.
   */
  @PreAuthorize("hasAuthority('SCOPE_Application.Write') or hasAuthority('APPROLE_ApplicationWriter')")
  public UUID createApplicationHistory(UUID applicationId, ApplicationHistoryRequestBody applicationHistoryRequestBody) {

    ApplicationHistoryEntity applicationHistoryEntity = new ApplicationHistoryEntity();

    applicationHistoryEntity.setApplicationId(applicationId);
    applicationHistoryEntity.setUserId(applicationHistoryRequestBody.getUserId());
    applicationHistoryEntity.setAction(applicationHistoryRequestBody.getAction().getValue());
    applicationHistoryEntity.setResourceTypeChanged(
        applicationHistoryRequestBody.getResourceTypeChanged().getValue());
    applicationHistoryEntity.setApplicationSnapshot(
        applicationHistoryRequestBody.getHistoricSnapshot());
    applicationHistoryEntity.setTimestamp(applicationHistoryRequestBody.getTimestamp().toInstant());

    if (applicationHistoryRequestBody.getResourceTypeChanged()
        == ApplicationResourceType.APPLICATION) {
      if (applicationHistoryRequestBody.getAction() == ActionType.CREATED) {
        applicationHistoryEntity.setHistoricSnapshot(
            applicationHistoryRequestBody.getHistoricSnapshot());

      } else if (applicationHistoryRequestBody.getAction() == ActionType.UPDATED) {
        ApplicationHistoryEntry currentApplicationHistory =
            getApplicationsLatestHistory(applicationId);

        Application oldVersion = objectMapper.convertValue(
            currentApplicationHistory.getApplicationSnapshot(),
            new TypeReference<>() {
            }
        );

        Application newVersion = objectMapper.convertValue(
            applicationHistoryRequestBody.getHistoricSnapshot(),
            new TypeReference<>() {
            }
        );

        Diff diff = javers.compare(oldVersion, newVersion);

        Map<Object, Map<String, Object>> changesByObject = new LinkedHashMap<>();

        Application applicationChanges = new Application();
        List<ApplicationProceeding> updatedProceedings = new ArrayList<>();

        for (ValueChange change : diff.getChangesByType(ValueChange.class)) {
          if (change.getAffectedObject().isPresent()) {
            Object cdo = change.getAffectedObject().get();

            Map<String, Object> fields = changesByObject.computeIfAbsent(cdo, k -> new LinkedHashMap<>());
            fields.put(change.getPropertyName(), change.getRight());

            Object id = getObjectId(cdo);
            if (id != null) {
              fields.putIfAbsent("id", id);
            }
          }
        }

        // Now apply changes
        for (Map.Entry<Object, Map<String, Object>> entry : changesByObject.entrySet()) {
          Object target = entry.getKey();
          Map<String, Object> fields = entry.getValue();

          if (target instanceof Application) {
            populateFields(applicationChanges, fields);
          } else if (target instanceof ApplicationProceeding) {
            ApplicationProceeding proceeding = new ApplicationProceeding();
            populateFields(proceeding, fields);
            updatedProceedings.add(proceeding);
          }
        }

        if (!updatedProceedings.isEmpty()) {
          applicationChanges.setProceedings(updatedProceedings);
        }

        // Convert to map for historic snapshot
        Map<String, Object> snapshot = objectMapper.convertValue(applicationChanges, new TypeReference<>() {});
        applicationHistoryEntity.setHistoricSnapshot(snapshot);
      }
    }
    applicationHistoryRepository.save(applicationHistoryEntity);
    return applicationHistoryEntity.getId();
  }

  protected ApplicationEntity checkIfApplicationExists(UUID id) {
    return applicationRepository
        .findById(id)
        .orElseThrow(
            () -> new ApplicationNotFoundException(String.format("No application found with id: %s", id)));
  }

  protected ApplicationHistoryEntity checkIfApplicationHistoryExists(UUID id) {
    return applicationHistoryRepository
        .findById(id)
        .orElseThrow(
            () -> new ApplicationNotFoundException(String.format("No application history found with id: %s", id)));
  }

  private Object getObjectId(Object object) {
    try {
      Field idField = object.getClass().getDeclaredField("id");
      idField.setAccessible(true);
      return idField.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return null;
    }
  }

  private void populateFields(Object target, Map<String, Object> fieldValues) {
    fieldValues.forEach((name, value) -> {
      try {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        // Optionally log or ignore unknown fields
      }
    });
  }

}
