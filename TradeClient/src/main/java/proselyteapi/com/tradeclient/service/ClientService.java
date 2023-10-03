package proselyteapi.com.tradeclient.service;

import jakarta.annotation.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;
import org.springframework.web.reactive.function.client.*;
import proselyteapi.com.tradeclient.model.*;
import proselyteapi.com.tradeclient.repository.*;
import proselyteapi.com.tradetrek.model.dto.*;
import reactor.core.publisher.*;
import reactor.core.scheduler.*;

import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ClientService {

    @Value("${company-url}")
    private String companyUrl;

    @Value("${stock-url}")
    private String stockUrl;

    private final WebClient webClient = WebClient.builder().build();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ClientStockRepository clientStockRepository;

    @PostConstruct
    public void init() {
        fetchAndProcessStockData();
    }


    public Mono<Void> fetchAndProcessStockData() {
        return fetchCompanies()
                .flatMap(companyDto -> fetchStock(companyDto.getSymbol())
                        .flatMap(stockDto -> clientStockRepository.findByCompanyName(companyDto.getName())
                                .flatMap(existingClientStock -> {
                                    existingClientStock.setOldPrice(existingClientStock.getNewPrice());
                                    existingClientStock.setNewPrice(stockDto.getPrice());
                                    return clientStockRepository.save(existingClientStock);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    ClientStock newClientStock = ClientStock.builder().build();
                                    newClientStock.setCompanyName(companyDto.getName());
                                    newClientStock.setOldPrice(stockDto.getPrice());
                                    newClientStock.setNewPrice(stockDto.getPrice());
                                    return clientStockRepository.save(newClientStock);
                                })))
                        .doFinally(signalType -> executorService.shutdown())
                        .subscribeOn(Schedulers.fromExecutor(executorService)))
                        .then();
    }

    private Mono<StockDto> fetchStock(String stockCode) {
        return webClient.get()
                .uri(stockUrl, stockCode)
                .retrieve()
                .bodyToMono(StockDto.class);
    }

    private Flux<CompanyDto> fetchCompanies() {
        return webClient.get()
                .uri(companyUrl)
                .retrieve()
                .bodyToFlux(CompanyDto.class);
    }
}
