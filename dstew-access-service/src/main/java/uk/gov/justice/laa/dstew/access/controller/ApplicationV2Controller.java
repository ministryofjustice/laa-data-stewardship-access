package uk.gov.justice.laa.dstew.access.controller;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.dstew.access.api.ApplicationV2Api;
import uk.gov.justice.laa.dstew.access.model.ApplicationV2;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodArguments;
import uk.gov.justice.laa.dstew.access.shared.logging.aspects.LogMethodResponse;

/**
 * Controller for handling /api/v2/applications requests.
 */
@RequiredArgsConstructor
@RestController
public class ApplicationV2Controller implements ApplicationV2Api {

  @LogMethodArguments
  @LogMethodResponse
  @Override
  public ResponseEntity<Void> createApplicationV2(ApplicationV2 applicationV2) {
    UUID id = UUID.randomUUID();
    // TODO: implement persistence.
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    return ResponseEntity.created(uri).build();
  }
}
