package proselyteapi.com.tradetrek.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "stock")
@Getter
@Setter
public class Stock {

    @Id
    private Long id;
    private Double price;
    private Long timestamp;

}
