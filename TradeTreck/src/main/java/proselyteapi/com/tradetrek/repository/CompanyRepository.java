package proselyteapi.com.tradetrek.repository;

import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.entity.*;
import reactor.core.publisher.*;

@Repository
public interface CompanyRepository extends ReactiveCrudRepository<Company, Long> {
    Mono<Company> findBySymbol(String symbol);
}
