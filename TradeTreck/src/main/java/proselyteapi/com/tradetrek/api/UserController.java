package proselyteapi.com.tradetrek.api;

import lombok.*;
import org.springframework.security.core.*;
import org.springframework.web.bind.annotation.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.security.*;
import proselyteapi.com.tradetrek.service.*;
import reactor.core.publisher.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final SecurityService securityService;
    private final UserService userService;

    @PostMapping("/register")
    public Mono<TokenDto> register(@RequestBody UserDto dto) {
        return userService.registerUser(dto).map(TokenDto::new);
    }

    @PostMapping("/login")
    public Mono<TokenDetails> login(@RequestBody UserDto dto) {
        return securityService.authenticate(dto.getUsername(), dto.getPassword());
    }

    @GetMapping("/api-key")
    public Mono<ApiKeyDto> getUserInfo(Authentication authentication) {
        return userService.getApiKey(((CustomPrincipal) authentication.getPrincipal()).getId()).map(ApiKeyDto::new);
    }
}


