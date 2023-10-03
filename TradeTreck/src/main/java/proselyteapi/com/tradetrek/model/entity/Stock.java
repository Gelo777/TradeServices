package proselyteapi.com.tradetrek.model.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.*;

@Table(name = "stock")
@Getter
@Setter
public class Stock {

    @Id
    private Long id;
    private Double price;
    private Long timestamp;

}
