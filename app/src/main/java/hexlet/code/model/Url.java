package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString

    public  class Url {

        private Long id;
        private String name;
        private Timestamp createdAt;

        public Url(String name, String model) {
            this.name = name;
            this.createdAt = getCreatedAt();
        }
    }
