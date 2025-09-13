package ru.practicum.explorewithme.api.client.event;

import java.util.Map;
import java.util.function.Function;
import ru.practicum.explorewithme.api.client.AbstractErrorDecoder;
import ru.practicum.explorewithme.api.error.BusinessRuleViolationException;
import ru.practicum.explorewithme.api.error.EntityNotFoundException;

public class EventClientErrorDecoder extends AbstractErrorDecoder {

    @Override
    protected Map<Integer, Function<String, Exception>> getSpecificErrorHandlers() {
        return Map.of(
            404, EntityNotFoundException::new,
            409, BusinessRuleViolationException::new
        );
    }
}