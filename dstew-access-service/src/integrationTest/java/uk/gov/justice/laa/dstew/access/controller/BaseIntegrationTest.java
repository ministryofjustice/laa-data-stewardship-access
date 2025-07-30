package uk.gov.justice.laa.dstew.access.controller;

import cloud.localstack.awssdkv1.TestUtils;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import com.amazonaws.services.sqs.AmazonSQS;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.justice.laa.dstew.access.AccessApp;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@SpringBootTest(classes = AccessApp.class, properties = "feature.disable-security=true")
@Testcontainers
public class BaseIntegrationTest {

    public static AmazonSQS amazonSQS;

    public static String queueUrl;

    @Container
    public static LocalStackContainer localStackContainer =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack:3"))
            .withServices(SQS)
            .withEnv("HOSTNAME_EXTERNAL", "localhost")
            .withEnv("DEFAULT_REGION", "eu-west-2")
            .withExposedPorts(4566);

    @Container
    @ServiceConnection
    public static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:14.3")
                .withDatabaseName("laa_db")
                .withUsername("laa_user")
                .withPassword("laa_password");

    static {
        setUpDatabase();
    }

    private static void setUpDatabase() {
        postgresContainer.start();
        System.setProperty("DB_PORT", postgresContainer.getFirstMappedPort().toString());
        System.out.println("DB_PORT: " + System.getProperty("DB_PORT"));
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // TODO  remove String endpoint...
        String endpoint = localStackContainer.getEndpointOverride(SQS).toString();
        registry.add("spring.cloud.aws.sqs.endpoint",
                () -> endpoint);
    }

    @BeforeAll
    static void beforeAll() {

        amazonSQS = AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
                        localStackContainer.getRegion())
                        )
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials("test", "test"))
                )
                .build();

        queueUrl = amazonSQS.createQueue("test-queue").getQueueUrl();
    }
}
