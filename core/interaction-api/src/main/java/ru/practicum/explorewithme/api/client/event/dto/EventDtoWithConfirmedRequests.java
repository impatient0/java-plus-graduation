package ru.practicum.explorewithme.api.client.event.dto;

public interface EventDtoWithConfirmedRequests {
    Long getId();
    void setConfirmedRequests(Long count);
}
