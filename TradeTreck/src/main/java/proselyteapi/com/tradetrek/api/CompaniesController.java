package proselyteapi.com.tradetrek.api;

import lombok.*;
import org.springframework.web.bind.annotation.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.service.*;
import reactor.core.publisher.*;

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
