package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public final class AppTest {

    private static MockWebServer mockWebServer;
    private static Javalin app;

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
    public void testPageUrl() throws Exception {
        var url = new Url("url=https://www.some-domain.com");
        UrlRepository.save(url);

        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        }));
    }

    @Test
    public void testPageUrls() throws Exception {
        var url = new Url("url=https://www.some-domain.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreate() throws SQLException {
        String inputUrl = "https://www.some-domain.com";

        System.out.println("Try to save the url: " + inputUrl);

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + inputUrl;
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
        });
        System.out.println("Read all urls from DB");

        List<Url> all = UrlRepository.getEntities();
        for (Url url : all) {
            System.out.println(url);
        }

        Url actualUrl = UrlRepository.findByName(inputUrl).orElse(null);

        System.out.println("actualUrl found by name:" + actualUrl);


        assertThat(actualUrl.getName()).isEqualTo(inputUrl);
    }

    @Test
    void testUrlNotFound() throws Exception {
        var url = new Url("https://www.some-domain.com");
        UrlRepository.deleteById(url.getId());

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());

            assertThat(response.code()).isEqualTo(404);
        });
    }
}
