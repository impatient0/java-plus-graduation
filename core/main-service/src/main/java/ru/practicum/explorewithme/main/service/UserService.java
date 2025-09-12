package ru.practicum.explorewithme.main.service;

import ru.practicum.explorewithme.api.dto.user.NewUserRequestDto;
import ru.practicum.explorewithme.api.dto.user.UserDto;
import ru.practicum.explorewithme.main.service.params.GetListUsersParameters;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto newUserDto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(GetListUsersParameters parameters);

}
