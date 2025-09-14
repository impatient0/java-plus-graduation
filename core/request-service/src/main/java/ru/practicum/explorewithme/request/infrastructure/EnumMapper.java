package ru.practicum.explorewithme.request.infrastructure;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.api.client.request.enums.RequestStatus;

@Mapper(componentModel = "spring")
public class EnumMapper {

    public RequestStatus toApiRequestStatus(ru.practicum.explorewithme.request.domain.RequestStatus modelStatus) {
        if (modelStatus == null) {
            return null;
        }
        return RequestStatus.valueOf(modelStatus.name());
    }

    public ru.practicum.explorewithme.request.domain.RequestStatus toModelRequestStatus(RequestStatus apiStatus) {
        if (apiStatus == null) {
            return null;
        }
        return ru.practicum.explorewithme.request.domain.RequestStatus.valueOf(apiStatus.name());
    }
}