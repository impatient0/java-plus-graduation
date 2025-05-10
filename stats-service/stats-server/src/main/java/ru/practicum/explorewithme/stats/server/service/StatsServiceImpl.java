package ru.practicum.explorewithme.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import ru.practicum.explorewithme.stats.server.repository.StatsRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.warn("STUB IMPLEMENTATION: StatsServiceImpl.saveHit called with DTO: {}", endpointHitDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.warn("STUB IMPLEMENTATION: StatsServiceImpl.getStats called with params: start={}, end={}, uris={}, unique={}",
            start, end, uris, unique);
        return Collections.emptyList();
    }
}