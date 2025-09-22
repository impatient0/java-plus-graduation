package ru.practicum.ewm.api.client.event.dto;

public interface EventDtoWithConfirmedRequests {
    Long getId();
    void setConfirmedRequests(Long count);
}
