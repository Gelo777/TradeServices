package proselyteapi.com.tradetrek.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.model.entity.*;
import proselyteapi.com.tradetrek.model.exception.*;
import proselyteapi.com.tradetrek.model.mapper.*;
import proselyteapi.com.tradetrek.repository.*;
import reactor.core.publisher.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final CompanyRepository companyRepository;
    private final StockMapper stockMapper;
    private final ReactiveValueOperations<String, Stock> stocksOperations;

    public Mono<StockDto> getStockBySymbol(String stockCode) {
        return stocksOperations.get(stockCode)
                .flatMap(stock -> Mono.just(stockMapper.toStockDto(stock)))
                .switchIfEmpty(
                        companyRepository.findBySymbol(stockCode)
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Компания не найдена")))
                                .flatMap(Company::getStock)
                                .flatMap(stock -> stocksOperations.set(stockCode, stock)
                                        .then(Mono.just(stock)))
                                .map(stockMapper::toStockDto)
                )
                .doOnNext(stockDto -> log.info("Fetched stock: {}", stockDto.getPrice()))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Запасы не найдены")));
    }
}