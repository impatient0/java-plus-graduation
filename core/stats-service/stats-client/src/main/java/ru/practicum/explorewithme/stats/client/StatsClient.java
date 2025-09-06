package ru.practicum.explorewithme.stats.client;

import static ru.practicum.explorewithme.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {

    @PostMapping("/hit")
    void saveHit(@RequestBody EndpointHitDto endpointHitDto);

    @GetMapping("/stats")
    List<ViewStatsDto> getStats(
        @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN) LocalDateTime start,
        @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN) LocalDateTime end,
        @RequestParam(value = "uris", required = false) List<String> uris,
        @RequestParam(value = "unique", defaultValue = "false") Boolean unique);
}
