package proselyteapi.com.tradetrek.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.model.entity.*;
import proselyteapi.com.tradetrek.model.mapper.*;
import proselyteapi.com.tradetrek.repository.*;
import reactor.core.publisher.*;
import reactor.test.*;

import static org.mockito.Mockito.*;

class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCompanies() {
        Company company1 = new Company();
        company1.setName("Company A");
        Company company2 = new Company();
        company2.setName("Company B");

        when(companyRepository.findAll()).thenReturn(Flux.just(company1, company2));
        when(companyMapper.toCompanyDto(company1)).thenReturn(CompanyDto.builder().build());
        when(companyMapper.toCompanyDto(company2)).thenReturn(CompanyDto.builder().build());

        Flux<CompanyDto> companyDtoFlux = companyService.getAllCompanies();
        StepVerifier.create(companyDtoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }
}
