package ru.practicum.explorewithme.api.client.event.dto;

import static ru.practicum.explorewithme.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.api.client.user.dto.UserShortDto;
import ru.practicum.explorewithme.api.client.event.enums.EventState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime eventDate;
    UserShortDto initiator;
    LocationDto location;
    boolean paid;
    int participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime publishedOn;
    boolean requestModeration;
    EventState state;
    String title;
    Long views;
    boolean commentsEnabled;
}