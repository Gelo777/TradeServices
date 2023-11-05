package proselyteapi.com.tradetrek.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proselyteapi.com.tradetrek.model.dto.StockDto;
import proselyteapi.com.tradetrek.service.StockService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks/{stock_code}/quote")
    public Mono<ResponseEntity<StockDto>> getCompanies(@PathVariable(value = "stock_code") String stockCode) {
        return stockService.getStockBySymbol(stockCode).map(ResponseEntity::ok);
    }
}
