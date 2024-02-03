package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter

public class UrlPage extends BasePage {
    public Url url;
    public long pageNumber;
    public List<UrlCheck> urlChecks;
    public String conditionNext;
    public String conditionBack;
}
