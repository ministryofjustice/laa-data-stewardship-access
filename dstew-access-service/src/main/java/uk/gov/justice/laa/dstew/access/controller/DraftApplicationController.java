package uk.gov.justice.laa.dstew.access.controller;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.dstew.access.api.DraftapplicationApi;
import uk.gov.justice.laa.dstew.access.model.DraftApplication;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodArguments;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodResponse;

/**
 * Controller for handling /api/v2/default-applications requests.
 */
@RequiredArgsConstructor
@RestController
public class DraftApplicationController implements DraftapplicationApi {
  @LogMethodArguments
  @LogMethodResponse
  @Override
  public ResponseEntity<Void> createDraftApplication(DraftApplication draftApplication) {
    UUID id = UUID.randomUUID();
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    return ResponseEntity.created(uri).build();
  }
}
