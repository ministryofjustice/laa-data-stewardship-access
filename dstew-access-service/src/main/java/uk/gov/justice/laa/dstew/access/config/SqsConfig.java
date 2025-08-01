package uk.gov.justice.laa.dstew.access.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

/**
 * Spring config class to provide injectable client for Amazon SQS.
 */
@Configuration
public class SqsConfig {

  private final SqsProperties sqsProperties;

  public SqsConfig(final SqsProperties sqsProperties) {
    this.sqsProperties = sqsProperties;
  }

  /**
   * Provides an injectable queue client.
   *
   * @return the queue client.
   */

  @Bean
  public SqsAsyncClient sqsAsyncClient() {
    String endPoint =  sqsProperties.getEndPoint();
    return SqsAsyncClient.builder()
        .endpointOverride(URI.create(endPoint))
        .region(Region.US_EAST_1)
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(sqsProperties.getAccessKey(), sqsProperties.getSecretKey())))
        .build();
  }
}
