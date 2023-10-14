package proselyteapi.com.tradetrek.config;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.web.server.*;
import org.springframework.security.web.server.*;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.util.matcher.*;
import org.springframework.web.server.*;
import proselyteapi.com.tradetrek.model.entity.*;
import proselyteapi.com.tradetrek.repository.*;
import proselyteapi.com.tradetrek.security.*;
import reactor.core.publisher.*;

import java.util.*;

import static org.springframework.security.config.Customizer.withDefaults;

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
    private final ReactiveRedisTemplate<String, Boolean> reactiveRedisTemplateApiKey;

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
                .oauth2Login(withDefaults())
                .formLogin(withDefaults())
                .build();
    }

    @Bean
    public WebFilter combinedWebFilter(RequestRateLimiter rateLimiter) {
        List<String> allowedUrls = Arrays.asList("/api/v1/register", "/api/v1/login");
        return (exchange, chain) ->
                rateLimiter.checkAndRegisterRequest()
                        .then(Mono.defer(() -> {
                            String apiKey = exchange.getRequest().getHeaders().getFirst("API-KEY");
                            String path = exchange.getRequest().getPath().value();
                            if (allowedUrls.contains(path)) {
                                return chain.filter(exchange);
                            } else {
                                ReactiveValueOperations<String, Boolean> valueOps = reactiveRedisTemplateApiKey.opsForValue();
                                return valueOps.get(apiKey)
                                        .flatMap(cachedResult -> {
                                            if (cachedResult != null && cachedResult) {
                                                return chain.filter(exchange);
                                            } else {
                                                return userRepository.existsByApiKey(apiKey)
                                                        .flatMap(valid -> {
                                                            if (Boolean.TRUE.equals(valid)) {
                                                                return valueOps.set(apiKey, true)
                                                                        .then(chain.filter(exchange));
                                                            } else {
                                                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                                                return exchange.getResponse().setComplete();
                                                            }
                                                        });
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
