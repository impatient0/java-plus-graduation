package ru.practicum.explorewithme.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.api.dto.event.ParticipationRequestDto;
import ru.practicum.explorewithme.main.model.ParticipationRequest;

@Mapper(componentModel = "spring", uses = EnumMapper.class)
public interface RequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "event.id", target = "eventId")
    ParticipationRequestDto toRequestDto(ParticipationRequest participationRequest);

}
