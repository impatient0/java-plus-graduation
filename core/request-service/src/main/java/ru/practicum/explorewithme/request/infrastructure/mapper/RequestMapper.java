package ru.practicum.explorewithme.request.infrastructure.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.api.client.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.domain.ParticipationRequest;

@Mapper(componentModel = "spring", uses = EnumMapper.class)
public interface RequestMapper {

    ParticipationRequestDto toRequestDto(ParticipationRequest participationRequest);

}
