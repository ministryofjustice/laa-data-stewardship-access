package uk.gov.justice.laa.dstew.access.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.dstew.access.api.ApplicationV1Api;
import uk.gov.justice.laa.dstew.access.model.ApplicationV1;
import uk.gov.justice.laa.dstew.access.model.ApplicationV1CreateReq;
import uk.gov.justice.laa.dstew.access.model.ApplicationV1UpdateReq;
import uk.gov.justice.laa.dstew.access.service.ApplicationService;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodArguments;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodResponse;

/**
 * Controller for handling /api/v1/applications requests.
 */
@RestController
@RequiredArgsConstructor
public class ApplicationV1Controller implements ApplicationV1Api {

  private final ApplicationService service;

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<List<ApplicationV1>> getApplications() {
    return ResponseEntity.ok(service.getAllApplications());
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<ApplicationV1> getApplicationById(UUID id) {
    return ResponseEntity.ok(service.getApplication(id));
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Void> createApplication(@RequestBody ApplicationV1CreateReq applicationCreateReq) {
    UUID id = service.createApplication(applicationCreateReq);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(id).toUri();
    return ResponseEntity.created(uri).build();
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Void> updateApplication(UUID id, ApplicationV1UpdateReq applicationUpdateReq) {
    service.updateApplication(id, applicationUpdateReq);
    return ResponseEntity.noContent().build();
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Void> deleteApplication(UUID id) {
    //service.deleteItem(id);
    return ResponseEntity.noContent().build();
  }
}
