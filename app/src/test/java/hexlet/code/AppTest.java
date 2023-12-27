package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public final class AppTest {

    private static MockWebServer mockWebServer;

    private static Javalin app;

    private UrlRepository urlRepository;

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader()
                .getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    @BeforeAll
    public static void beforeAll() throws Exception {

        mockWebServer = new MockWebServer();
        MockResponse mockedResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(readResourceFile("index.html"));
        mockWebServer.enqueue(mockedResponse);
        mockWebServer.start();
    }

    @BeforeEach
    public void setUp() throws Exception {
        app = App.getApp();
    }

    @Test
    public void testMainPage() throws Exception {

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testPageUrl() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("url=http://www.some-domain.com", Timestamp.valueOf(LocalDateTime.now()));
            UrlRepository.save(url);

            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()
                    .string()).contains("http://www.some-domain.com");
        });
    }

    @Test
    public void testPageUrls() throws Exception {
        var url = new Url("url=http://www.some-domain.com", Timestamp.valueOf(LocalDateTime.now()));
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()
                    .string()).contains("http://www.some-domain.com");
        });
    }

    @Test
    public void testStore() throws SQLException {
        String inputUrl = "https://www.some-domain.com";

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + inputUrl;
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
        });

        Url actualUrl = UrlRepository.findByName(inputUrl);

        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo(inputUrl);
    }

    @Test
    void testUrlNotFound() throws Exception {
        Url url = new Url("https://www.some-domain.com");
        UrlRepository.deleteById(url.getId());

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());

            assertThat(response.code()).isEqualTo(404);
        });
    }
}
