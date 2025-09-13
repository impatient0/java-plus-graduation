package ru.practicum.explorewithme.comment.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class AdminCommentSearchParams {
    private final Long userId;
    private final Long eventId;
    private final Boolean isDeleted;
}