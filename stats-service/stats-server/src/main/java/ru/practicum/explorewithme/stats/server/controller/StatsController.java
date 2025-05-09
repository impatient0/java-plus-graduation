package ru.practicum.explorewithme.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import ru.practicum.explorewithme.stats.server.service.StatsService;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class StatsController {

    private final StatsService statsService;

    /**
     * Сохранение информации о том, что к эндпоинту был запрос
     *
     * @param endpointHitDto данные запроса
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("STUB: Received POST /hit request with DTO: {}", endpointHitDto);
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
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime start,

        @RequestParam(name = "end")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime end,

        @RequestParam(name = "uris", required = false) List<String> uris,
        @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {

        log.info("STUB: Received GET /stats request with params: start={}, end={}, uris={}, unique={}",
            start, end, uris, unique);

        return Collections.emptyList();
    }
}