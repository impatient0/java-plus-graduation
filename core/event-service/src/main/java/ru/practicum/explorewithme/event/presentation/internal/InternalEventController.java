package ru.practicum.explorewithme.event.presentation.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.api.client.event.EventClient;
import ru.practicum.explorewithme.api.client.event.dto.EventInternalDto;
import ru.practicum.explorewithme.event.application.EventService;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class InternalEventController implements EventClient {

    private final EventService eventService;

    @Override
    @GetMapping("/{eventId}")
    public EventInternalDto getEventById(Long eventId) {
        return eventService.getEventById(eventId);
    }
}
