package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import java.net.URL;

public final class App {

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:Hexlet;DB_CLOSE_DELAY=-1;");
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    private static int getPort() {
        String port = System.getenv()
                .getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }

    public static Javalin getApp() {

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World!"));
        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
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

    public static String urlBuild(URL url) {
        String protocol = url.getProtocol() == null ? "" : url.getProtocol();
        String host = url.getHost();
        String port = url.getPort() == -1 ? "" : ":" + url.getPort();
        String specialSymbols = "://";

        return protocol + specialSymbols + host + port;
    }
}
