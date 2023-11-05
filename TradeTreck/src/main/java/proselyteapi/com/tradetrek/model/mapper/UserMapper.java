package proselyteapi.com.tradetrek.model.mapper;

import org.mapstruct.Mapper;
import proselyteapi.com.tradetrek.model.dto.UserDto;
import proselyteapi.com.tradetrek.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);
}
