package proselyteapi.com.tradetrek.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import proselyteapi.com.tradetrek.model.entity.Company;
import reactor.core.publisher.Mono;

@Repository
public interface CompanyRepository extends ReactiveCrudRepository<Company, Long> {
    Mono<Company> findBySymbol(String symbol);
}
