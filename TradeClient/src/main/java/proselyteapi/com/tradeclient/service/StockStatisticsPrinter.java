package proselyteapi.com.tradeclient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import proselyteapi.com.tradeclient.model.ClientStock;
import proselyteapi.com.tradeclient.repository.ClientStockRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockStatisticsPrinter {
    private final ClientStockRepository clientStockRepository;

    @Scheduled(fixedRate = 5000)
    public void schedulePrintStatistics() {
        printStatistics();
    }

    public void printStatistics() {
        List<ClientStock> topStocks = clientStockRepository.findTop5ByOrderByNewPriceDesc();

        List<ClientStock> last5Stocks = clientStockRepository.findTop5ByOrderByPriceChangePercentageDesc();

        System.out.println("Топ 5 акций с наивысшей стоимостью:");
        for (ClientStock stock : topStocks) {
            System.out.println(stock.getCompanyName() + " - " + stock.getNewPrice());
        }

        System.out.println("\nПоследние 5 компаний с наибольшим процентным изменением стоимости акций:");
        for (ClientStock stock : last5Stocks) {
            System.out.println(stock.getCompanyName() + " - " + stock.getNewPrice());
        }
    }
}
