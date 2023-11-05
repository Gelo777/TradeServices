package proselyteapi.com.tradetrek.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import proselyteapi.com.tradeclient.model.ClientStock;
import proselyteapi.com.tradeclient.repository.ClientStockRepository;
import proselyteapi.com.tradeclient.service.StockStatisticsPrinter;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StockStatisticsPrinterTest {

    @Mock
    private ClientStockRepository clientStockRepository;

    private StockStatisticsPrinter stockStatisticsPrinter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        stockStatisticsPrinter = new StockStatisticsPrinter(clientStockRepository);
    }

    @Test
    public void testPrintStatistics() {
        List<ClientStock> topStocks = createTestStocks();
        List<ClientStock> last5Stocks = createTestStocks();

        when(clientStockRepository.findTop5ByOrderByNewPriceDesc()).thenReturn(topStocks);
        when(clientStockRepository.findTop5ByOrderByPriceChangePercentageDesc()).thenReturn(last5Stocks);

        stockStatisticsPrinter.printStatistics();

        verify(clientStockRepository, times(1)).findTop5ByOrderByNewPriceDesc();
        verify(clientStockRepository, times(1)).findTop5ByOrderByPriceChangePercentageDesc();
    }

    private List<ClientStock> createTestStocks() {
        List<ClientStock> stocks = Arrays.asList(
                ClientStock.builder().companyName("Company1").newPrice(100.0).build(),
                ClientStock.builder().companyName("Company2").newPrice(200.0).build(),
                ClientStock.builder().companyName("Company3").newPrice(150.0).build(),
                ClientStock.builder().companyName("Company4").newPrice(300.0).build(),
                ClientStock.builder().companyName("Company5").newPrice(250.0).build()
        );
        return stocks;
    }
}
