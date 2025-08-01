package uk.gov.justice.laa.dstew.access.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Service class for handling cloud.aws.sqs properties.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloud-platform.aws.sqs")
public class SqsProperties {

  private String endPoint;
  private String accessKey;
  private String secretKey;
}