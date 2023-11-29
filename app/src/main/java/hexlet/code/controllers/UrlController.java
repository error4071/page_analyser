package hexlet.code.controllers;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;


import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;

import java.sql.Timestamp;

import static hexlet.code.App.urlBuild;

public  class UrlController {

    public static void addUrl(Context ctx) throws SQLException {
        var name = ctx.formParamAsClass("url", String.class).get().toLowerCase().trim();

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
}
