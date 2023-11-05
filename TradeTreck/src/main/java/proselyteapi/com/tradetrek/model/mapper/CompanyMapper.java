package proselyteapi.com.tradetrek.model.mapper;

import org.mapstruct.Mapper;
import proselyteapi.com.tradetrek.model.dto.CompanyDto;
import proselyteapi.com.tradetrek.model.entity.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyDto toCompanyDto(Company company);
}
