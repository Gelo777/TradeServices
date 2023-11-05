package proselyteapi.com.tradetrek.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proselyteapi.com.tradetrek.model.dto.CompanyDto;
import proselyteapi.com.tradetrek.model.exception.EntityNotFoundException;
import proselyteapi.com.tradetrek.model.mapper.CompanyMapper;
import proselyteapi.com.tradetrek.repository.CompanyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
