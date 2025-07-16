package uk.gov.justice.laa.dstew.access.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.justice.laa.dstew.access.AccessApp;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@SpringBootTest(classes = AccessApp.class, properties = "feature.disable-security=true")
@AutoConfigureMockMvc
@Transactional
@Testcontainers
public class BaseIntegrationTest {

    private static DockerImageName LOCALSTACK_IMAGE =
            DockerImageName.parse("localstack/localstack:3");

    @Container
    public static LocalStackContainer localStackContainer =
            new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(SQS);

    @Container
    @ServiceConnection
    public static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:14.3")
                .withDatabaseName("laa_db")
                .withUsername("laa_user")
                .withPassword("laa_password")
                .withExposedPorts(5432);

    static {
        setUpDatabase();
    }

    private static void setUpDatabase() {
        postgresContainer.start();
        System.setProperty("DB_PORT", postgresContainer.getFirstMappedPort().toString());
        System.out.println("DB_PORT: " + System.getProperty("DB_PORT"));
    }
}
