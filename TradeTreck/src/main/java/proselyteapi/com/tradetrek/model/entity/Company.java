package proselyteapi.com.tradetrek.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

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
