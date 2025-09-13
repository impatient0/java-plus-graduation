package ru.practicum.explorewithme.api.client.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.api.client.event.dto.EventFullDto;

@FeignClient(name = "main-service", path = "/internal/events", configuration = EventClientConfiguration.class)
public interface EventClient {

    @GetMapping("/{eventId}")
    EventFullDto getEventById(@PathVariable("eventId") Long eventId);
}
