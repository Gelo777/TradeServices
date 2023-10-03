package proselyteapi.com.tradetrek.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class StockDto {

    private Long id;
    private Double price;
    private Long timestamp;

}
