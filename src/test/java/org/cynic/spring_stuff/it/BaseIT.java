package org.cynic.spring_stuff.it;

import io.restassured.RestAssured;
import java.time.Clock;
import org.cynic.spring_stuff.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {Configuration.class, Configuration.SecurityAutoConfiguration.class}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("it")
@Tag("it")
public class BaseIT {

    protected static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpc3MiLCJpYXQiOjE2OTkyNzY0MTksImV4cCI6MTczMDgxMjQxOSwiYXVkIjoiYXVkIiwic3ViIjoic3ViIiwiZW1haWwiOiJraXJpbEB6ZW5pdGVjaC5jby51ayJ9.4HTv-jfUQRu1RhSNf8pjcxL6xarSL7RYKuCzxAT311c";

    @LocalServerPort
    private int serverPort;

    @Autowired
    protected Clock clock;

    @BeforeAll
    public void setup() {
        RestAssured.port = serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
