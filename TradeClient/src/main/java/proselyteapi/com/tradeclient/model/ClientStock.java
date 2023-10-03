package proselyteapi.com.tradeclient.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.*;

@Table(name = "client_stock")
@Getter
@Setter
@Builder
public class ClientStock {

    private String companyName;
    private Double oldPrice;
    private Double newPrice;
}
