package proselyteapi.com.tradetrek.model.mapper;

import org.mapstruct.Mapper;
import proselyteapi.com.tradetrek.model.dto.StockDto;
import proselyteapi.com.tradetrek.model.entity.Stock;

@Mapper(componentModel = "spring")
public interface StockMapper {

    StockDto toStockDto(Stock stock);
}
