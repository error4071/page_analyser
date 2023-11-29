package hexlet.code.utils;

public class NamedRoutes {
    public static String rootPath() {
        return "/";
    }

    public static String urlsPath(Long id) {
        return urlsPath(String.valueOf(id));
    }

    public static String urlsPath(String id) {
        return "/urls/" + id;
    }

    public static String urlsPath() {
        return "/urls";
    }

    public static String aboutPath() {
        return "/about";
    }

    public static String urlBuildPath() {
        return "/urls/new";
    }
}
