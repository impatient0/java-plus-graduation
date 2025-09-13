package ru.practicum.explorewithme.user.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.api.client.user.dto.NewUserRequestDto;
import ru.practicum.explorewithme.api.client.user.dto.UserDto;
import ru.practicum.explorewithme.api.error.EntityAlreadyExistsException;
import ru.practicum.explorewithme.api.error.EntityNotFoundException;
import ru.practicum.explorewithme.user.domain.User;
import ru.practicum.explorewithme.user.domain.UserRepository;
import ru.practicum.explorewithme.user.infrastructure.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequestDto newUserDto) {

        if (userRepository.existsByEmail(newUserDto.getEmail())) {
            throw new EntityAlreadyExistsException("User", "email", newUserDto.getEmail());
        }

        return userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserDto)));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {

        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isEmpty()) {
            throw new EntityNotFoundException("User", "Id", userId);
        }

        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(GetListUsersParameters parameters) {

        List<UserDto> result;

        if (parameters.getIds() == null || parameters.getIds().isEmpty()) {
            result = userRepository.findAll(parameters.getFrom(), parameters.getSize()).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            result = userRepository.findAllByIdIn(parameters.getIds(), parameters.getFrom(),
                    parameters.getSize()).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return userRepository.findByIdIn(ids).stream().map(userMapper::toUserDto)
            .collect(Collectors.toList());
    }

    @Override
    public void checkUserExists(Long userId) {

        if (userId == null || !userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", "Id", userId);
        }
    }

    @Override
    public UserDto getUser(Long userId) {

        return userMapper.toUserDto(userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User", "Id", userId)));
    }

}
