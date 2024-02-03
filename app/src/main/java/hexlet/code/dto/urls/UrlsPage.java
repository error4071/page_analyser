package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter

public class UrlsPage extends BasePage {
    private Long pageNumber;
    private List<Url> pagedUrls;
    private List<UrlCheck> lastCheck;
    private String conditionNext;
    private String conditionBack;

    public UrlsPage(List<Url> pagedUrls, Long pageNumber, List<UrlCheck> lastCheck,
                    String conditionNext, String conditionBack) {
        super();
        this.pagedUrls = pagedUrls;
        this.pageNumber = pageNumber;
        this.lastCheck = lastCheck;
        this.conditionNext = conditionNext;
        this.conditionBack = conditionBack;
    }
}
