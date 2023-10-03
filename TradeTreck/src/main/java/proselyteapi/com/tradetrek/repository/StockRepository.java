package proselyteapi.com.tradetrek.repository;

import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.entity.*;

@Repository
public interface StockRepository extends ReactiveCrudRepository<Stock, Long> {
}
