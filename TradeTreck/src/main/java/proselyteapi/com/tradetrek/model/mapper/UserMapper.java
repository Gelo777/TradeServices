package proselyteapi.com.tradetrek.model.mapper;

import org.mapstruct.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.model.entity.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);
}
