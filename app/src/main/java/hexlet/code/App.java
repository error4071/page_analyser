package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public final class App {

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:hexlet_project;DB_CLOSE_DELAY=-1;");
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    private static String readResourceFile(String fileName) throws IOException {
        var path = Paths.get("src", "main", "resources", fileName);
        return Files.readString(path);
    }

    private static int getPort() {
        String port = System.getenv()
                .getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }

    public static Javalin getApp() throws IOException, SQLException {

        JavalinJte.init(createTemplateEngine());

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        if (isProduction()) {
            String username = System.getenv("JDBC_DATABASE_USERNAME");
            hikariConfig.setUsername(username);
            String password = System.getenv("JDBC_DATABASE_PASSWORD");
            hikariConfig.setPassword(password);
        }
        var dataSource = new HikariDataSource(hikariConfig);

        var schemaSql = readResourceFile("schema.sql");

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(schemaSql);
        }

        BaseRepository.dataSource = dataSource;


        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        JavalinJte.init(createTemplateEngine());

        app.get(NamedRoutes.rootPath(), RootController::index);
        app.get(NamedRoutes.urlsPath(), UrlController::showUrls);
        app.post(NamedRoutes.urlsPath(), UrlController::addUrl);
        app.get(NamedRoutes.urlPath("{id}"), UrlController::showUrl);
        app.post(NamedRoutes.urlCheckPath("{id}"), UrlController::checkUrl);

        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("jte", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static void addRoutes(Javalin app) {
        app.get(NamedRoutes.rootPath(), RootController::index);
        app.post(NamedRoutes.urlsPath(), UrlController::addUrl);
    }

    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        app.start(getPort());
    }
}
