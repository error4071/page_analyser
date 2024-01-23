package hexlet.code.utils;

import java.net.URL;

public class BuildUrl {
    public static String urlBuild(URL url) {
        String protocol = url.getProtocol() == null ? "" : url.getProtocol();
        String host = url.getHost();
        String port = url.getPort() == -1 ? "" : ":" + url.getPort();
        String specialSymbols = "://";

        return protocol + specialSymbols + host + port;
    }
}