package ru.practicum.explorewithme.api.client.user;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.explorewithme.api.error.EntityNotFoundException;

public class UserClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 404) {
            return new EntityNotFoundException("Entity not found in remote user-service.");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}