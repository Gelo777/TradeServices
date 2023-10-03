package proselyteapi.com.tradetrek.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.redis.core.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.model.entity.*;
import proselyteapi.com.tradetrek.model.mapper.*;
import proselyteapi.com.tradetrek.repository.*;
import reactor.core.publisher.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StockServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private StockMapper stockMapper;

    @Mock
    private ReactiveValueOperations<String, Stock> stocksOperations;

    @InjectMocks
    private StockService stockService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStockBySymbol() {
        Company company = new Company();
        Stock stock = new Stock();
        stock.setPrice(50.0);
        company.setStock(Mono.just(stock));

        when(stocksOperations.get(anyString())).thenReturn(Mono.just(stock));
        when(companyRepository.findBySymbol("AAPL")).thenReturn(Mono.just(company));
        when(stockMapper.toStockDto(stock)).thenReturn(StockDto.builder().build() );

        StockDto stockDtoMono = stockService.getStockBySymbol("AAPL").block();

    }
}
