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

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static hexlet.code.repository.BaseRepository.dataSource;

public class UrlController {
    public static void addUrl(Context ctx) throws SQLException {
        var name = ctx.formParamAsClass("url", String.class)
                .get()
                .toLowerCase()
                .trim();

        if (name.isEmpty()) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        if (!UrlRepository.existsByName(name)) {
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            var url = new Url(name, createdAt);

            UrlRepository.save(url);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            ctx.sessionAttribute("flash", "Страница уже добавлена");
            ctx.sessionAttribute("flash-type", "info");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }
    public static void showUrls(Context ctx) throws SQLException {

        List<Url> urls = UrlRepository.getEntities();
        Map<Long, UrlCheck> urlChecks = UrlController.findLatestChecks();
        var page = new UrlsPage(urls, urlChecks);

        var pageNumber = ctx.queryParamAsClass("page", long.class)
                .getOrDefault(1L);
        var per = 15;
        var firstPost = (pageNumber - 1) * per;

        List<Url> pagedUrls = urls.stream()
                .skip(firstPost)
                .limit(per)
                .collect(Collectors.toList());

        List<UrlCheck> lastCheck = new ArrayList<>();

        pagedUrls.forEach(url -> {
            try {
                lastCheck.add(UrlRepositoryCheck.getLastCheck(url.getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        String conditionNext = UrlRepository.getEntities()
                .size() > pageNumber * per
                ? "active" : "disabled";
        String conditionBack = pageNumber > 1 ? "active" : "disabled";

        var pages = new UrlsPage(pagedUrls, pageNumber, lastCheck, conditionNext, conditionBack);
        pages.setFlash(ctx.consumeSessionAttribute("flash"));
        pages.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", pages));
    }

    public static void showUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class)
                .get();
        var pageNumber = ctx.queryParamAsClass("page", long.class)
                .getOrDefault(1L);
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        var urlChecks = UrlRepositoryCheck.getEntities(id);

        final long urlPerPage = 5;

        String conditionNext = UrlRepositoryCheck.getEntities(id)
                .size() > pageNumber * urlPerPage
                ? "active" : "disabled";
        String conditionBack = pageNumber > 1 ? "active" : "disabled";

        var page = new UrlPage(url, pageNumber, urlChecks, conditionNext, conditionBack);
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
            ctx.redirect(NamedRoutes.urlPath(id));
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Неверный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }

    public static void urlBuild(Context ctx) throws SQLException {
        var inputUrl = ctx.formParam("url");
        URL parsedUrl;
        try {
            parsedUrl = new URL(inputUrl);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        String normalizedUrl = String
                .format(
                        "%s://%s%s",
                        parsedUrl.getProtocol(),
                        parsedUrl.getHost(),
                        parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort()
                )
                .toLowerCase();

        Url url = UrlRepository.findByName(normalizedUrl);

        if (url != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
        } else {
            Url newUrl = new Url(normalizedUrl, new Timestamp(System.currentTimeMillis()));
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
        }

        ctx.redirect("/urls");
    }

    public static Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        var sql = "SELECT DISTINCT ON (url_id) * from url_checks order by url_id DESC, id DESC";
        try (var conn = dataSource.getConnection();
            var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new HashMap<Long, UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var urlId = resultSet.getLong("url_id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var check = new UrlCheck(statusCode, title, h1, description, urlId, createdAt);
                check.setId(id);
                check.setUrlId(urlId);
                check.setCreatedAt(createdAt);
                result.put(urlId, check);
            }
            return result;
        }
    }
}

