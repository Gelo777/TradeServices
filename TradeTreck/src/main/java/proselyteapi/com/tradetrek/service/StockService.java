package proselyteapi.com.tradetrek.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import proselyteapi.com.tradetrek.model.dto.StockDto;
import proselyteapi.com.tradetrek.model.entity.Company;
import proselyteapi.com.tradetrek.model.entity.Stock;
import proselyteapi.com.tradetrek.model.exception.EntityNotFoundException;
import proselyteapi.com.tradetrek.model.mapper.StockMapper;
import proselyteapi.com.tradetrek.repository.CompanyRepository;
import reactor.core.publisher.Mono;

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