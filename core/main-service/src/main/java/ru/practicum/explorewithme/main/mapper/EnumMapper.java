package ru.practicum.explorewithme.main.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.api.client.event.enums.EventState;
import ru.practicum.explorewithme.api.client.request.enums.RequestStatus;

@Mapper(componentModel = "spring")
public class EnumMapper {

    public RequestStatus toApiRequestStatus(ru.practicum.explorewithme.main.model.RequestStatus modelStatus) {
        if (modelStatus == null) {
            return null;
        }
        return RequestStatus.valueOf(modelStatus.name());
    }

    public ru.practicum.explorewithme.main.model.RequestStatus toModelRequestStatus(RequestStatus apiStatus) {
        if (apiStatus == null) {
            return null;
        }
        return ru.practicum.explorewithme.main.model.RequestStatus.valueOf(apiStatus.name());
    }

    public EventState toApiEventState(ru.practicum.explorewithme.main.model.EventState eventState) {
        if (eventState == null) {
            return null;
        }
        return EventState.valueOf(eventState.name());
    }

    public ru.practicum.explorewithme.main.model.EventState toModelEventState(EventState eventState) {
        if (eventState == null) {
            return null;
        }
        return ru.practicum.explorewithme.main.model.EventState.valueOf(eventState.name());
    }
}