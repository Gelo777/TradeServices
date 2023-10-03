package proselyteapi.com.tradetrek.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.model.exception.*;
import proselyteapi.com.tradetrek.model.mapper.*;
import proselyteapi.com.tradetrek.repository.*;
import reactor.core.publisher.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;


    public Flux<CompanyDto> getAllCompanies() {
        return companyRepository.findAll()
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Список компаний пустой")))
                .map(companyMapper::toCompanyDto)
                .doOnNext(companyDto -> log.info("Получена компания: {}", companyDto.getName()))
                .doOnError(error -> log.error("Ошибка при получении компаний: {}", error.getMessage()));
    }
}
