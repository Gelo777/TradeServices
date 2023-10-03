package proselyteapi.com.tradetrek.repository;

import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.entity.*;
import reactor.core.publisher.*;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<Boolean> existsByApiKey(String apiKey);
    Mono<User> findByUsername(String username);

}
