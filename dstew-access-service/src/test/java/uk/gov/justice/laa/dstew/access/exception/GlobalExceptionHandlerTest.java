package uk.gov.justice.laa.dstew.access.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.laa.dstew.access.validation.ValidationException;

class GlobalExceptionHandlerTest {

  GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  @Test
  void handleApplicationNotFound_returnsNotFoundStatusAndErrorMessage() throws Exception {
    ResponseEntity<ProblemDetail> result = globalExceptionHandler
        .handleApplicationNotFound(new ApplicationNotFoundException("Application not found"));

    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getDetail()).isEqualTo("Application not found");
  }

  void handleValidationException_returnsBadRequestStatusAndErrors() throws Exception {
    ResponseEntity<ProblemDetail> result = globalExceptionHandler
        .handleValidationException(new ValidationException(List.of("error1")));

    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(result.getBody()).isNotNull();
    //noinspection unchecked
    assertThat(((List<String>) result.getBody().getProperties().get("errors")).getFirst()).isEqualTo("error1");
  }

  @Test
  void handleGenericException_returnsInternalServerErrorStatusAndErrorMessage() throws Exception {
    ResponseEntity<ProblemDetail> result = globalExceptionHandler.handleGenericException(new RuntimeException("Something went wrong"));

    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getDetail()).isEqualTo("An unexpected application error has occurred.");
  }
}
