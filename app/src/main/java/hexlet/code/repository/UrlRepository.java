package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.sql.Statement;

public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, createdAt) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
            var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        preparedStatement.setString(1, url.getName());
        preparedStatement.setTimestamp(2, url.getCreatedAt());
        preparedStatement.executeUpdate();

        var generatedKeys = preparedStatement.getGeneratedKeys();

        if (generatedKeys.next()) {
            url.setId(generatedKeys.getLong(1));
        } else {
            throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }
}
