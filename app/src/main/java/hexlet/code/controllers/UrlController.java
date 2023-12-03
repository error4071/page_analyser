package hexlet.code.controllers;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import hexlet.code.repository.UrlRepositoryCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static hexlet.code.App.urlBuild;

public  class UrlController {

    public static void addUrl(Context ctx) throws SQLException {
        var name = ctx.formParamAsClass("url", String.class)
                .get()
                .toLowerCase()
                .trim();

        String normaliseUrl;

        try {
            var urlToValidate = new URI(name).toURL();
            normaliseUrl = urlBuild(urlToValidate);
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        if (!UrlRepository.existsByName(normaliseUrl)) {
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            var url = new Url(normaliseUrl, createdAt);

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
        var urls = UrlRepository.getEntities();
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

        var page = new UrlsPage(pagedUrls, pageNumber, lastCheck, conditionNext, conditionBack);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
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
}
