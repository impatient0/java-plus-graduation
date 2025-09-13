package ru.practicum.explorewithme.user.infrastructure;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.api.client.user.dto.NewUserRequestDto;
import ru.practicum.explorewithme.api.client.user.dto.UserDto;
import ru.practicum.explorewithme.api.client.user.dto.UserShortDto;
import ru.practicum.explorewithme.user.domain.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserShortDto toShortDto(User user);

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequestDto newUserDto);

    User toUser(UserDto userDto);

}
