package uk.gov.justice.laa.dstew.access.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.dstew.access.api.ApplicationHistoryApi;
import uk.gov.justice.laa.dstew.access.model.ApplicationHistoryEntry;
import uk.gov.justice.laa.dstew.access.model.ApplicationHistoryRequestBody;
import uk.gov.justice.laa.dstew.access.service.ApplicationService;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodArguments;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodResponse;

/**
 * Controller for handling application requests.
 */
@RestController
@RequiredArgsConstructor
public class ApplicationHistoryController implements ApplicationHistoryApi {

  private final ApplicationService service;

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Void> recordApplicationHistory(
      final UUID id,
      final ApplicationHistoryRequestBody applicationHistoryRequestBody) {
    return null;
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<List<ApplicationHistoryEntry>> getApplicationHistory(final UUID applicationId) {
    return ResponseEntity.ok(service.getAllApplicationHistory(applicationId));
  }

  @LogMethodResponse
  @LogMethodArguments
  @Override
  public ResponseEntity<ApplicationHistoryEntry> getApplicationHistoryById(final UUID applicationId, final UUID id) {
    return null;
  }

  @LogMethodResponse
  @LogMethodArguments
  @Override
  public ResponseEntity<ApplicationHistoryEntry> getLatestApplicationHistory(
      final UUID applicationId) {
    ApplicationHistoryEntry latestHistory = service.getApplicationsLatestHistory(applicationId);
    return ResponseEntity.ok(latestHistory);
  }


}
