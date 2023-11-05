package proselyteapi.com.tradetrek.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import proselyteapi.com.tradetrek.model.entity.Stock;

@Repository
public interface StockRepository extends ReactiveCrudRepository<Stock, Long> {
}
