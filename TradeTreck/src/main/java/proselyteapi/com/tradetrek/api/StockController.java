package proselyteapi.com.tradetrek.api;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.service.*;
import reactor.core.publisher.*;

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
