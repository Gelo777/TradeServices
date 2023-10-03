package proselyteapi.com.tradetrek.security;

import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.exception.*;
import reactor.core.publisher.*;
import reactor.core.scheduler.*;

import java.time.*;
import java.util.concurrent.*;

@Component
public class RequestRateLimiter {

    private final Semaphore semaphore;
    private final int permits;
    private final Duration interval;

    public RequestRateLimiter() {
        this.permits = 150;
        this.interval = Duration.ofSeconds(1);
        this.semaphore = new Semaphore(permits);
    }

    public Mono<Void> checkAndRegisterRequest() {
        return Mono.defer(() -> {
            if (semaphore.tryAcquire()) {
                return Mono.delay(interval, Schedulers.boundedElastic())
                        .doOnNext(t -> semaphore.release())
                        .then();
            } else {
                return Mono.error(new TooManyRequestsException("Слишком много запросов"));
            }
        });
    }
}

