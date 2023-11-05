package proselyteapi.com.tradetrek.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockDto {

    private Long id;
    private Double price;
    private Long timestamp;

}
