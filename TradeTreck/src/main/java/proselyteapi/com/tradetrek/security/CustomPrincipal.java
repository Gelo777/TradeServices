package proselyteapi.com.tradetrek.security;

import lombok.*;

import java.security.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Principal {
    private Long id;
    private String name;
}
