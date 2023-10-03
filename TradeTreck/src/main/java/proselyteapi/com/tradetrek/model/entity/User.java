package proselyteapi.com.tradetrek.model.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.*;

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