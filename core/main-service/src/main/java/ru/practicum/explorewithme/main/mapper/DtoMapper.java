package ru.practicum.explorewithme.main.mapper;


import org.mapstruct.Mapper;
import ru.practicum.explorewithme.api.client.user.dto.UserDto;
import ru.practicum.explorewithme.api.client.user.dto.UserShortDto;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    UserShortDto toUserShortDto(UserDto userDto);

}
