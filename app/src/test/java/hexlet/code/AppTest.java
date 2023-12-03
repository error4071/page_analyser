package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AppTest {

    private static MockWebServer mockWebServer;

    private static Javalin app;

    @BeforeEach
    public void setUp() throws Exception {
        app = App.getApp();
    }

    @Test
    void testMainPage() throws Exception {

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testPageUrl() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url( "url=http://www.some-domain.com", Timestamp.valueOf(LocalDateTime.now()));
            UrlRepository.save(url);

            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("http://www.some-domain.com");
        });
    }

    @Test
    public void testPageUrls() throws Exception {
        var url = new Url( "url=http://www.some-domain.com", Timestamp.valueOf(LocalDateTime.now()));
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("http://www.some-domain.com");
        });
    }

    @Test
    public void testCreateUrl() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://www.some-domain.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            var url = UrlRepository.find(1L).orElseThrow(() -> new NotFoundResponse("Url not found"));

            String urlId = String.valueOf(url.getId());
            String urlName = url.getName();

            assertThat(response.body().string()).contains(urlId, urlName);
        });
    }

    @Test
    void testUrlNotFound() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }
}
