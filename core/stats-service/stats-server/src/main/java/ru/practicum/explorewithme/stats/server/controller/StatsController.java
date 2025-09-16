package ru.practicum.explorewithme.stats.server.controller;

import static ru.practicum.explorewithme.api.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.stats.client.StatsClient;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;
import ru.practicum.explorewithme.stats.server.service.StatsService;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class StatsController implements StatsClient {

    private final StatsService statsService;

    /**
     * Сохранение информации о том, что к эндпоинту был запрос
     *
     * @param endpointHitDto данные запроса
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Controller: request to save new hit received.");
        log.debug("Saving new hit: {}", endpointHitDto);
        statsService.saveHit(endpointHitDto);
    }

    /**
     * Получение статистики по посещениям.
     *
     * @param start  Дата и время начала диапазона (в формате "yyyy-MM-dd HH:mm:ss")
     * @param end    Дата и время конца диапазона (в формате "yyyy-MM-dd HH:mm:ss")
     * @param uris   Список uri для которых нужно выгрузить статистику (опционально)
     * @param unique Нужно ли учитывать только уникальные посещения (опционально, default: false)
     * @return Список ViewStatsDto со статистикой
     */
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(
        @RequestParam(name = "start")
        @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
        LocalDateTime start,

        @RequestParam(name = "end")
        @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
        LocalDateTime end,

        @RequestParam(name = "uris", required = false) List<String> uris,
        @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {

        log.info("Controller: request to retrieve stats received.");
        log.debug("Request params: start={}, end={}, uris={}, unique={}",
            start, end, uris, unique);

        return statsService.getStats(start, end, uris, unique);
    }
}