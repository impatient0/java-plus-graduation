package ru.practicum.explorewithme.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.api.client.event.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.main.model.ParticipationRequest;

@Mapper(componentModel = "spring", uses = EnumMapper.class)
public interface RequestMapper {

    @Mapping(source = "event.id", target = "eventId")
    ParticipationRequestDto toRequestDto(ParticipationRequest participationRequest);

}
