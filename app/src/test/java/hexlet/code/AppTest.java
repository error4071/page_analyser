package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlRepositoryCheck;
import hexlet.code.utils.NamedRoutes;
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
    public static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        MockResponse mockedResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(readResourceFile("index.html"));
        mockWebServer.enqueue(mockedResponse);
        mockWebServer.start();
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlsPage() throws Exception {
        var url = new Url("url=https://www.some-domain.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.some-domain.com");
        });
    }

    @Test
    public void testStore() throws SQLException {
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

        Url actualUrl = UrlRepository.findByName(inputUrl)
                .orElse(null);

        System.out.println("actualUrl found by name:" + actualUrl);

        assertThat(actualUrl).isNotNull();
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

    @Test
    public void testCheckShow() throws SQLException {
        var url = new Url("https://www.some-domain.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()
                    .string())
                    .contains("Анализатор страниц")
                    .contains("https://www.some-domain.com");
        });
    }

    @Test
    public void testCheckEmpty() throws Exception {
        var checkUrl = new Url("https://www.some-domain.com").toString();
        var url = new Url(checkUrl);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/1/checks");
            assertThat(response.body()
                    .string()).doesNotContain("Анализатор страниц");
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.some-domain.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()
                    .string()).contains("https://www.some-domain.com");
        });
    }

    @Test
    public void testUrlWithNoAttrCheck() {
        var mockServerUrl = mockWebServer.url("/").toString();

        var mockResponse = new MockResponse();
        var mockContent = "<p>some paragraph</p>";
        mockResponse.setBody(mockContent);
        mockWebServer.enqueue(mockResponse);

        JavalinTest.test(app, ((server, client) -> {
            var url = new Url(mockServerUrl);
            UrlRepository.save(url);
            var id = url.getId();

            var response = client.post(NamedRoutes.urlCheckPath(String.valueOf(id)));
            assertThat(response.code()).isEqualTo(200);
            var responseBody = response.body().string();
            assertThat(responseBody.contains("200"));

            var urlCheck = UrlRepositoryCheck.getEntities(1L).get(0);
            assertThat(urlCheck.getId() == 1L);
            assertThat(urlCheck.getId().equals(id));
            assertThat(urlCheck.getCreatedAt()).isNotNull();

            assertThat(urlCheck.getTitle()).isEqualTo("");
            assertThat(urlCheck.getH1()).isEqualTo("");
            assertThat(urlCheck.getDescription()).isEqualTo("");
        }));
    }
}
}
