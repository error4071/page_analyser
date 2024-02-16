package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import io.javalin.http.Context;

import java.util.Collections;

public final class RootController {

    public static void welcome(Context ctx) {
        var page = new MainPage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", Collections.singletonMap("page", page));
    };
}
