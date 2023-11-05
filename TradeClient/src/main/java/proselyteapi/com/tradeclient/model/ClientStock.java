package proselyteapi.com.tradeclient.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "client_stock")
@Getter
@Setter
@Builder
public class ClientStock {

    private String companyName;
    private Double oldPrice;
    private Double newPrice;
}
