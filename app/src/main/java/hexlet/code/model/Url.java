package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@ToString

public class Url {
    private long id;
    private String name;
    private Timestamp createdAt;
    private List<UrlCheck> urlChecks;
    private UrlCheck lastCheck;

    public Url(String name) {
        this.name = name;
    }
}
