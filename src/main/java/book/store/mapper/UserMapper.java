package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel (UserRegistrationRequestDto registrationRequestDto);

    UserResponseDto toDto (User user);
}
