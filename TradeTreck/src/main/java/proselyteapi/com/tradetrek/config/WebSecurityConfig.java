package proselyteapi.com.tradetrek.config;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.web.server.*;
import org.springframework.security.web.server.*;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.util.matcher.*;
import org.springframework.web.server.*;
import proselyteapi.com.tradetrek.repository.*;
import proselyteapi.com.tradetrek.security.*;
import reactor.core.publisher.*;

import java.util.*;

@Slf4j
@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String secret;
    private final String[] publicRoutes = {"/api/v1/register", "/api/v1//login"};
    private final UserRepository userRepository;
    private final RequestRateLimiter requestRateLimiter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(publicRoutes).permitAll()
                .anyExchange().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> handleAuthenticationError(swe, HttpStatus.UNAUTHORIZED, "unauthorized error"))
                .accessDeniedHandler((swe, e) -> handleAuthenticationError(swe, HttpStatus.FORBIDDEN, "access denied"))
                .and()
                .addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(combinedWebFilter(requestRateLimiter), SecurityWebFiltersOrder.FIRST)
                .build();
    }

    @Bean
    public WebFilter combinedWebFilter(RequestRateLimiter rateLimiter) {
        List<String> allowedUrls = Arrays.asList("/api/v1/register", "/api/v1/login");

        return (exchange, chain) ->
                rateLimiter.checkAndRegisterRequest()
                        .then(Mono.defer(() -> {
                            if (allowedUrls.contains(exchange.getRequest().getPath().value())) {
                                return chain.filter(exchange);
                            } else {
                                return userRepository.existsByApiKey(exchange.getRequest().getHeaders().getFirst("API-KEY"))
                                        .flatMap(valid -> {
                                            if (Boolean.TRUE.equals(valid)) {
                                                return chain.filter(exchange);
                                            } else {
                                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                                return exchange.getResponse().setComplete();
                                            }
                                        });
                            }
                        }));
    }

    private Mono<Void> handleAuthenticationError(ServerWebExchange swe, HttpStatus status, String errorMessage) {
        log.error("IN securityWebFilterChain - {}: {}", status, errorMessage);
        swe.getResponse().setStatusCode(status);
        return swe.getResponse().setComplete();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        bearerAuthenticationFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return bearerAuthenticationFilter;
    }
}
