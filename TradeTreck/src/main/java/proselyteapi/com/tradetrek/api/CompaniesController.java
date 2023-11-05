package proselyteapi.com.tradetrek.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proselyteapi.com.tradetrek.model.dto.CompanyDto;
import proselyteapi.com.tradetrek.service.CompanyService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CompaniesController {

    private final CompanyService companyService;

    @GetMapping("/companies")
    public Flux<CompanyDto> getCompanies() {
        return companyService.getAllCompanies();
    }
}
