package proselyteapi.com.tradetrek.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import proselyteapi.com.tradetrek.model.exception.UnauthorizedException;
import proselyteapi.com.tradetrek.service.UserService;
import reactor.core.publisher.Mono;

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
