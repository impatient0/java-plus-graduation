package ru.practicum.explorewithme.infra.gateway.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/service-fallback")
    public Mono<Map<String, String>> serviceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "The requested service is currently unavailable. Please try again later.");
        return Mono.just(response);
    }
}
