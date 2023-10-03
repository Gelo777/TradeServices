package proselyteapi.com.tradetrek.model.dto;

import lombok.*;
import proselyteapi.com.tradetrek.model.entity.*;
import reactor.core.publisher.*;

@Getter
@Setter
@Builder
public class CompanyDto {
    private Long id;
    private String symbol;
    private String name;
    private Mono<Stock> stock;
}
