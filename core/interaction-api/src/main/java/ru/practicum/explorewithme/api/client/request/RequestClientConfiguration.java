package ru.practicum.explorewithme.api.client.request;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import ru.practicum.explorewithme.api.client.event.EventClientErrorDecoder;

public class RequestClientConfiguration {

    @Bean
    public ErrorDecoder requestClientErrorDecoder() {
        return new RequestClientErrorDecoder();
    }
}
