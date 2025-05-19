package uk.gov.justice.laa.access.datastore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.access.datastore.model.ApplicationHistoryMessage;
import uk.gov.justice.laa.access.datastore.model.ApplicationHistoryRequestBody;
import uk.gov.justice.laa.access.datastore.service.ApplicationService;

@Component
@RequiredArgsConstructor
public class SqsConsumer {

  private final ObjectMapper objectMapper;
  private final ApplicationService applicationService;

  @SqsListener("test-queue")
  public void receiveMessage(String message) {
    try {
      ApplicationHistoryMessage historyMessage =
          objectMapper.readValue(message, ApplicationHistoryMessage.class);

      System.out.println("✅ Received ApplicationHistoryMessage: " + historyMessage);

      //do a get request for the application then store that in json.
      ApplicationHistoryRequestBody applicationHistoryRequestBody =
          ApplicationHistoryRequestBody
              .builder()
              .historicSnapshot(historyMessage.getHistoricSnapshot())
              .userId(historyMessage.getUserId())
              .action(historyMessage.getAction())
              .resourceTypeChanged(historyMessage.getResourceTypeChanged())
              .timestamp(historyMessage.getTimestamp())
              .build();

      //post to application history endpoint
      //we skip that and we can call the service directly
      applicationService.createApplicationHistory(historyMessage.getApplicationId(), applicationHistoryRequestBody);

    } catch (Exception e) {
      System.err.println("❌ Failed to parse SQS message: " + e.getMessage());
    }
  }
}
