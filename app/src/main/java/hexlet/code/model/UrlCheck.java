package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Setter

public class UrlCheck {

    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Long id;
    private Timestamp createdAt;

    public UrlCheck() {
    }
    public void setUrlId(long urlId) {
    }
}
