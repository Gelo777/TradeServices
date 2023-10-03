package proselyteapi.com.tradetrek.service;

import jakarta.annotation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.entity.*;
import proselyteapi.com.tradetrek.repository.*;
import reactor.core.publisher.*;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

    private final StockRepository stockRepository;
    private final CompanyRepository companyRepository;
    private final ReactiveRedisTemplate<String, Stock> reactiveRedisTemplate;


    private Random random = new Random();

    @PostConstruct
    public void initializeStockPrices() {
        log.info("Инициализация начальных цен акций");

        Stock stock1 = new Stock();
        stock1.setPrice(generateRandomInitialPrice());

        Stock stock2 = new Stock();
        stock2.setPrice(generateRandomInitialPrice());

        Company company1 = new Company();
        company1.setName("Company A");
        company1.setStock(Mono.just(stock1));
        companyRepository.save(company1);

        Company company2 = new Company();
        company2.setName("Company B");
        company2.setStock(Mono.just(stock2));
        companyRepository.save(company2);
    }

    @Scheduled(cron = "0 * * * * *")
    public void updateStockPrices() {
        updateCache();
        stockRepository.findAll()
                .flatMap(this::updateStockPrice)
                .subscribe(stock -> log.info("Обновлена цена акции для {}: {}", stock.getId(), stock.getPrice()));

    }

    private Mono<Stock> updateStockPrice(Stock stock) {
        double currentPrice = stock.getPrice();
        double newPrice = generateNewPrice(currentPrice);
        stock.setPrice(newPrice);
        return stockRepository.save(stock);
    }

    @CacheEvict(value = "stockCache", allEntries = true)
    public void updateCache() {
        reactiveRedisTemplate.keys("*")
                .flatMap(reactiveRedisTemplate::delete)
                .doOnNext(count -> log.info("Очищено {} записей из кеша", count))
                .subscribe();
    }

    private double generateRandomInitialPrice() {
        double minInitialPrice = 10.0;
        double maxInitialPrice = 100.0;

        return minInitialPrice + (maxInitialPrice - minInitialPrice) * random.nextDouble();
    }

    private double generateNewPrice(double currentPrice) {
        double minChangePercent = -0.10;
        double maxChangePercent = 0.10;

        double priceChangePercent = minChangePercent + (maxChangePercent - minChangePercent) * random.nextDouble();

        double newPrice = currentPrice * (1 + priceChangePercent);

        return Math.max(newPrice, 0);
    }
}
