package proselyteapi.com.tradetrek.security;

import lombok.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.exception.*;
import proselyteapi.com.tradetrek.service.*;
import reactor.core.publisher.*;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getApiKey(principal.getId())
                .flatMap(user -> Mono.just(authentication))
                .switchIfEmpty(Mono.error(new UnauthorizedException("Unauthorized")));
    }
}
