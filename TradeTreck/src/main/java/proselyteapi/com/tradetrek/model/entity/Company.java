package proselyteapi.com.tradetrek.model.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.*;
import reactor.core.publisher.*;

@Table(name = "companies")
@Getter
@Setter
public class Company {

    @Id
    private Long id;
    private String symbol;
    private String name;
    private Mono<Stock> stock;
}
