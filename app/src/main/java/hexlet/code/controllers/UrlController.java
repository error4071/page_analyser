package hexlet.code.controllers;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlRepositoryCheck;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlController {
    public static void createUrl(Context ctx) throws SQLException {
        var inputUrl = ctx.formParam("url");
        System.out.println("createUrl: " + inputUrl);
        URI parsedUrl;
        try {
            parsedUrl = new URI(inputUrl);
        } catch (Exception e) {
            System.out.println("Некорректный url: " + inputUrl);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        // Нормализируем урл.
        // Нужны только протокол, имя домена и порт (если задан).
        // В случае дефолтного порта 80, его указывать не требуется
        String normalizedUrl = String
                .format(
                        "%s://%s%s",
                        parsedUrl.getScheme(),
                        parsedUrl.getHost(),
                        parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort()
                )
                .toLowerCase();

        Url url = UrlRepository.findByName(normalizedUrl).orElse(null);
        System.out.println("normalized url: " + normalizedUrl);

        if (url != null) {
            System.out.println("url уже существует и не будет сохранён");
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
        } else {

            Url newUrl = new Url(normalizedUrl);
            UrlRepository.save(newUrl);
            System.out.println("url успешно сохранен в БД");
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
        }

        ctx.redirect("/urls");
    }

    public static void showUrls(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var pageNumber = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        var per = 10;
        var firstPost = (pageNumber - 1) * per;

        List<Url> pagedUrls = urls.stream()
                .skip(firstPost)
                .limit(per)
                .collect(Collectors.toList());

        pagedUrls.forEach(url -> {
            try {
                url.setLastCheck(UrlRepositoryCheck.getLastCheck(url.getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        var page = new UrlsPage(pagedUrls, pageNumber);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void showUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        var urlChecks = UrlRepositoryCheck.getEntities(id);
        url.setUrlChecks(urlChecks);

        var page = new UrlPage(url);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void checkUrl(Context ctx) throws SQLException {
        long id = ctx.pathParamAsClass("id", Long.class)
                .get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        try {
            HttpResponse<String> response = Unirest.get(url.getName())
                    .asString();

            var statusCode = response.getStatus();

            Document doc = Jsoup.parse(response.getBody());
            String title = doc.title();
            Element h1Element = doc.selectFirst("h1");
            String h1 = h1Element != null ? h1Element.text() : "";
            Element descElement = doc.selectFirst("meta[name=description]");
            String description = descElement != null ? descElement.attr("content") : "";

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());

            var urlCheck = new UrlCheck(statusCode, title, h1, description, id, createdAt);
            UrlRepositoryCheck.save(urlCheck);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Неверный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));
        }
    }
}
