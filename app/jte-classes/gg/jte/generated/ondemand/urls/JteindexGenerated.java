package gg.jte.generated.ondemand.urls;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.utils.NamedRoutes;
public final class JteindexGenerated {
	public static final String JTE_NAME = "urls/index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,3,3,3,20,20,20,23,23,23,26,26,26,26,26,26,26,28,28,30,30,30,33,33,33,35,35,40,40,42,42,48,48,48,48,49,49,49,49,55,55,55,55,55,55,55,55,55,55,55,57,57,57,57,58,58,58,58,65};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, UrlsPage page) {
		jteOutput.writeContent("\r\n\r\n    <div class=\"container-lg mt-5\">\r\n\r\n        <h1>Сайты</h1>\r\n\r\n        <table class=\"table table-bordered table-hover mt-3\">\r\n            <thead>\r\n            <tr>\r\n                <th class=\"col-1\">ID</th>\r\n                <th>Имя</th>\r\n                <th class=\"col-2\">Последняя проверка</th>\r\n                <th class=\"col-1\">Код ответа</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody>\r\n            ");
		for (var url : page.getUrls()) {
			jteOutput.writeContent("\r\n                <tr>\r\n                    <td>\r\n                        ");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(url.getId());
			jteOutput.writeContent("\r\n                    </td>\r\n                    <td>\r\n                        <a href=\"/urls/");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(url.getId());
			jteOutput.setContext("a", null);
			jteOutput.writeContent("\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(url.getName());
			jteOutput.writeContent("</a>\r\n                    </td>\r\n                    ");
			if ((page.getLastCheck().stream().anyMatch(check -> check.getUrlId() != null && check.getUrlId() == url.getId()))) {
				jteOutput.writeContent("\r\n                        <td>\r\n                            ");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(page.getLastCheck().stream().filter(check -> check.getUrlId() == url.getId()).findFirst().get().getCreatedAt().toString().substring(0,16));
				jteOutput.writeContent("\r\n                        </td>\r\n                        <td>\r\n                            ");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(page.getLastCheck().stream().filter(check -> check.getUrlId() == url.getId()).findFirst().get().getStatusCode());
				jteOutput.writeContent("\r\n                        </td>\r\n                    ");
			} else {
				jteOutput.writeContent("\r\n                        <td>\r\n                        </td>\r\n                        <td>\r\n                        </td>\r\n                    ");
			}
			jteOutput.writeContent("\r\n                </tr>\r\n            ");
		}
		jteOutput.writeContent("\r\n            </tbody>\r\n        </table>\r\n\r\n        <nav aria-label=\"Page navigation\">\r\n            <ul class=\"pagination justify-content-center mt-5\">\r\n                <li class=\"page-item ");
		jteOutput.setContext("li", "class");
		jteOutput.writeUserContent(page.getConditionBack());
		jteOutput.setContext("li", null);
		jteOutput.writeContent("\">\r\n                    <a class=\"page-link\" href=\"?page=");
		jteOutput.setContext("a", "href");
		jteOutput.writeUserContent(page.getPageNumber() < 2 ? 1 : page.getPageNumber() - 1);
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\"\r\n                       aria-label=\"Previous\">\r\n                        <span aria-hidden=\"true\">&laquo;</span>\r\n                    </a>\r\n                </li>\r\n                <li class=\"page-item active\"><a class=\"page-link\"\r\n                                                href=\"");
		jteOutput.setContext("a", "href");
		jteOutput.writeUserContent(NamedRoutes.urlsPath());
		jteOutput.setContext("a", null);
		jteOutput.writeContent("?page=");
		jteOutput.setContext("a", "href");
		jteOutput.writeUserContent(page.getPageNumber());
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(page.getPageNumber());
		jteOutput.writeContent("</a>\r\n                </li>\r\n                <li class=\"page-item ");
		jteOutput.setContext("li", "class");
		jteOutput.writeUserContent(page.getConditionNext());
		jteOutput.setContext("li", null);
		jteOutput.writeContent("\">\r\n                    <a class=\"page-link\" href=\"?page=");
		jteOutput.setContext("a", "href");
		jteOutput.writeUserContent(page.getPageNumber() + 1);
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\" aria-label=\"Next\">\r\n                        <span aria-hidden=\"true\">&raquo;</span>\r\n                    </a>\r\n                </li>\r\n            </ul>\r\n        </nav>\r\n    </div>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		UrlsPage page = (UrlsPage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, page);
	}
}
