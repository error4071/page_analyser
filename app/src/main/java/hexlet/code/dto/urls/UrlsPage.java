package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter

public class UrlsPage extends BasePage {
    private Long pageNumber;
    private List<Url> pagedUrls;
    private List<Url> urls;
    private Map<Long, UrlCheck> latestChecks;
    private List<UrlCheck> lastCheck;
    private String conditionNext;
    private String conditionBack;

    public UrlsPage(List<Url> pagedUrls, Long pageNumber, List<UrlCheck> lastCheck,
                    String conditionNext, String conditionBack) {
    }
}
