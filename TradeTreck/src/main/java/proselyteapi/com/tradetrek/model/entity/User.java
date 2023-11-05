package proselyteapi.com.tradetrek.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
@Getter
@Setter
public class User{

    @Id
    private Long id;
    private String username;
    private String password;
    private String apiKey;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}