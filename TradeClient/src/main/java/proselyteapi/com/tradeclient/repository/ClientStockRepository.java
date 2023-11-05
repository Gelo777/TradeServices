package proselyteapi.com.tradeclient.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import proselyteapi.com.tradeclient.model.ClientStock;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClientStockRepository extends ReactiveCrudRepository<ClientStock, Long> {

    Mono<ClientStock> findByCompanyName(String companyName);

    @Query("SELECT s FROM ClientStock s ORDER BY s.newPrice DESC LIMIT 5")
    List<ClientStock> findTop5ByOrderByNewPriceDesc();
    @Query("SELECT s FROM ClientStock s ORDER BY ((s.newPrice - s.oldPrice) / s.oldPrice) DESC NULLS LAST")
    List<ClientStock> findTop5ByOrderByPriceChangePercentageDesc();
}
