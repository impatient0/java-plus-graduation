package ru.practicum.explorewithme.api.client.comment.dto;

import ru.practicum.explorewithme.api.client.user.dto.UserShortDto;

public interface CommentDtoWithAuthor {

    void setAuthor(UserShortDto author);
}
