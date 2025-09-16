package ru.practicum.explorewithme.event.infrastructure.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.api.client.event.enums.EventState;

@Mapper(componentModel = "spring")
public class EnumMapper {

    public EventState toApiEventState(ru.practicum.explorewithme.event.domain.EventState eventState) {
        if (eventState == null) {
            return null;
        }
        return EventState.valueOf(eventState.name());
    }

    public ru.practicum.explorewithme.event.domain.EventState toModelEventState(EventState eventState) {
        if (eventState == null) {
            return null;
        }
        return ru.practicum.explorewithme.event.domain.EventState.valueOf(eventState.name());
    }
}