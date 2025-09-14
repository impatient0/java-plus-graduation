package ru.practicum.explorewithme.event.application.params;

import java.time.LocalDateTime;

public interface SearchParamsWithDateRange {
    public LocalDateTime getRangeStart();
    public LocalDateTime getRangeEnd();
}
