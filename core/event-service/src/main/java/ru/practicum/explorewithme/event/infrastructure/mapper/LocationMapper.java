package ru.practicum.explorewithme.event.infrastructure.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.api.client.event.dto.LocationDto;
import ru.practicum.explorewithme.event.domain.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toLocationDto(Location location);

    Location toLocation(LocationDto locationDto);
}