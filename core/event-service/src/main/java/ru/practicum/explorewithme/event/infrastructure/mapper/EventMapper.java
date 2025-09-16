package ru.practicum.explorewithme.event.infrastructure.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.explorewithme.api.client.event.dto.EventFullDto;
import ru.practicum.explorewithme.api.client.event.dto.EventInternalDto;
import ru.practicum.explorewithme.api.client.event.dto.EventShortDto;
import ru.practicum.explorewithme.api.client.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.domain.Event;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, LocationMapper.class, EnumMapper.class})
public interface EventMapper {

    @Mappings({
        @Mapping(target = "confirmedRequests", ignore = true),
        @Mapping(target = "views", ignore = true),
        @Mapping(target = "initiator", ignore = true)
    })
    EventFullDto toEventFullDto(Event event);

    EventInternalDto toEventInternalDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "state", expression = "java(ru.practicum.explorewithme.event.domain.EventState.PENDING)")
    Event toEvent(NewEventDto newEventDto);

    List<EventFullDto> toEventFullDtoList(List<Event> events);

    @Mappings({
        @Mapping(target = "confirmedRequests", ignore = true),
        @Mapping(target = "views", ignore = true),
        @Mapping(target = "initiator", ignore = true)
    })
    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> toEventShortDtoList(List<Event> events);
}
