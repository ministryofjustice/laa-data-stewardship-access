package uk.gov.justice.laa.dstew.access.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.dstew.access.model.ApplicationV1HistoryCreateReq;
import uk.gov.justice.laa.dstew.access.model.ApplicationV1HistoryMessage;
import uk.gov.justice.laa.dstew.access.service.ApplicationService;

/**
 * Consumes messages from a queue.
 */
@Component
@RequiredArgsConstructor
public class SqsConsumer {

  private final ObjectMapper objectMapper;
  private final ApplicationService applicationService;

  /**
   * Message listener for the SQS queue.
   *
   * @param message the message being received.
   */
  @SqsListener("test-queue")
  public void receiveMessage(String message) {
    try {
      var historyMessage = objectMapper.readValue(message, ApplicationV1HistoryMessage.class);

      System.out.println("✅ Received ApplicationHistoryMessage: " + historyMessage);

      //do a get request for the application then store that in json.
      var applicationHistoryCreateReq = ApplicationV1HistoryCreateReq
              .builder()
              .historicSnapshot(historyMessage.getHistoricSnapshot())
              .userId(historyMessage.getUserId())
              .action(historyMessage.getAction())
              .resourceTypeChanged(historyMessage.getResourceTypeChanged())
              .timestamp(historyMessage.getTimestamp())
              .build();

      //post to application history endpoint
      //we skip that and we can call the service directly
      applicationService.createApplicationHistory(historyMessage.getApplicationId(), applicationHistoryCreateReq);
    } catch (Exception e) {
      System.err.println("❌ Failed to parse SQS message: " + e.getMessage());
    }
  }
}
