package proselyteapi.com.tradetrek.api;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import proselyteapi.com.tradetrek.model.exception.AuthException;
import proselyteapi.com.tradetrek.model.exception.EntityNotFoundException;
import proselyteapi.com.tradetrek.model.exception.TokenExpiredException;
import proselyteapi.com.tradetrek.model.exception.TooManyRequestsException;
import proselyteapi.com.tradetrek.model.exception.UnauthorizedException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {


    private static final Map<Class<? extends RuntimeException>, HttpStatus> exceptionStatusMap = new HashMap<>();

    static {
        exceptionStatusMap.put(UnauthorizedException.class, HttpStatus.UNAUTHORIZED);
        exceptionStatusMap.put(TooManyRequestsException.class, HttpStatus.TOO_MANY_REQUESTS);
        exceptionStatusMap.put(TokenExpiredException.class, HttpStatus.UNAUTHORIZED);
        exceptionStatusMap.put(EntityNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionStatusMap.put(AuthException.class, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = determineHttpStatus(ex);
        String errorMessage = ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : "Internal server error";
        return handleException(exchange, status, errorMessage);
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        return exceptionStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<Void> handleException(ServerWebExchange exchange, HttpStatus status, String errorMessage) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = "{\"error\": \"" + errorMessage + "\"}";
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes())));
    }
}
