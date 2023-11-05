package proselyteapi.com.tradetrek.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import proselyteapi.com.tradetrek.repository.UserRepository;
import proselyteapi.com.tradetrek.security.AuthenticationManager;
import proselyteapi.com.tradetrek.security.BearerTokenServerAuthenticationConverter;
import proselyteapi.com.tradetrek.security.JwtHandler;
import proselyteapi.com.tradetrek.security.RequestRateLimiter;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

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

                        Mono<Boolean> cachedResultMono = valueOps.get(apiKey).defaultIfEmpty(null);
                        Mono<Boolean> userExistsMono = userRepository.existsByApiKey(apiKey);

                        return Mono.zip(cachedResultMono, userExistsMono)
                            .flatMap(tuple -> {
                                Boolean cachedResult = tuple.getT1();
                                Boolean userExists = tuple.getT2();

                                if (cachedResult != null && cachedResult) {
                                    return chain.filter(exchange);
                                } else if (Boolean.TRUE.equals(userExists)) {
                                    return valueOps.set(apiKey, true)
                                        .then(chain.filter(exchange));
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
