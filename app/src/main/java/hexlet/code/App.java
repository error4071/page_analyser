package hexlet.code;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controllers.UrlController;
import hexlet.code.controller.UrlCheckController;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;
@Slf4j

public class App {

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }
    private static final String JDBC_DATA_H2 = "jdbc:h2:mem:project";
    static String jdbcUrlCurrent = getJdbcDatabaseUrl();

    public static String getJdbcDatabaseUrl() {
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = JDBC_DATA_H2;
        }
        return jdbcUrl;
    }

    public static Javalin getApp() throws IOException, SQLException {

        JavalinJte.init(createTemplateEngine());
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrlCurrent);

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        var url = App.class.getClassLoader().getResource("schema.sql");
        var file = new File(url.getFile());
        var sql = Files.lines(file.toPath())
                .collect(Collectors.joining("\n"));

        log.info(sql);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });
        app.get("/", ctx -> {
            ctx.render("index.jte");
        });
        app.get("/urls", UrlController::addUrl);
        app.get("/urls/{id}", UrlController::showUrl);
        app.post("/urls", UrlController::addUrl);
        app.post("/urls/{id}/checks", UrlController::checkUrl);

        return app;
    }

    public static void main(String[] args) throws IOException, SQLException {
        Javalin app = getApp();
        app.start(getPort());
    }
}