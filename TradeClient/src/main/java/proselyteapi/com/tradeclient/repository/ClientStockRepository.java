package proselyteapi.com.tradeclient.repository;

import org.springframework.data.r2dbc.repository.*;
import org.springframework.data.repository.reactive.*;
import proselyteapi.com.tradeclient.model.*;
import reactor.core.publisher.*;

import java.util.*;

public interface ClientStockRepository extends ReactiveCrudRepository<ClientStock, Long> {

    Mono<ClientStock> findByCompanyName(String companyName);

    @Query("SELECT s FROM ClientStock s ORDER BY s.newPrice DESC LIMIT 5")
    List<ClientStock> findTop5ByOrderByNewPriceDesc();
    @Query("SELECT s FROM ClientStock s ORDER BY ((s.newPrice - s.oldPrice) / s.oldPrice) DESC NULLS LAST")
    List<ClientStock> findTop5ByOrderByPriceChangePercentageDesc();
}
