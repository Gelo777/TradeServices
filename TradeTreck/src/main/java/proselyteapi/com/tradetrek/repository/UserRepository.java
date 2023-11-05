package proselyteapi.com.tradetrek.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import proselyteapi.com.tradetrek.model.entity.User;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<Boolean> existsByApiKey(String apiKey);
    Mono<User> findByUsername(String username);

}
