package uk.gov.justice.laa.access.datastore.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.access.datastore.api.ApplicationsApi;
import uk.gov.justice.laa.access.datastore.common.logging.aspects.LogMethodArguments;
import uk.gov.justice.laa.access.datastore.common.logging.aspects.LogMethodResponse;
import uk.gov.justice.laa.access.datastore.model.Application;
import uk.gov.justice.laa.access.datastore.model.ApplicationRequestBody;
import uk.gov.justice.laa.access.datastore.model.ApplicationUpdateRequestBody;
import uk.gov.justice.laa.access.datastore.service.ApplicationService;

/**
 * Controller for handling application requests.
 */
@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationsApi {

  private final ApplicationService service;

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<List<Application>> getApplications() {
    return ResponseEntity.ok(service.getAllApplications());
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Application> getApplicationById(UUID id) {
    return ResponseEntity.ok(service.getApplication(id));
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Void> createApplication(@RequestBody ApplicationRequestBody applicationRequestBody) {
    UUID id = service.createApplication(applicationRequestBody);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(id).toUri();
    return ResponseEntity.created(uri).build();
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Void> updateApplication(UUID id, ApplicationUpdateRequestBody applicationUpdateRequestBody) {
    service.updateApplication(id, applicationUpdateRequestBody);
    return ResponseEntity.noContent().build();
  }

  @Override
  @LogMethodResponse
  @LogMethodArguments
  public ResponseEntity<Void> deleteApplication(UUID id) {
    //service.deleteItem(id);
    //return ResponseEntity.noContent().build();
    return null;
  }


}
