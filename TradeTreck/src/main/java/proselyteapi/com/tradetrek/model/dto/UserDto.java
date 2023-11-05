package proselyteapi.com.tradetrek.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
}
