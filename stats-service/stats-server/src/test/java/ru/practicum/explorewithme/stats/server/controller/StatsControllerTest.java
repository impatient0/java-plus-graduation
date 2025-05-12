package ru.practicum.explorewithme.stats.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;
import ru.practicum.explorewithme.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    @InjectMocks
    private StatsController statsController;

    @Mock
    private StatsService statsService;

    private MockMvc mvc;

    private EndpointHitDto validHitDto;
    private LocalDateTime now;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mvc = MockMvcBuilders
                .standaloneSetup(statsController)
                .build();
        now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        validHitDto = EndpointHitDto.builder()
                .app("test-app")
                .uri("/test-uri")
                .ip("127.0.0.1")
                .timestamp(now.minusHours(1))
                .build();
    }

    @Test
    void saveHit_whenDtoIsValid_shouldReturnCreated() throws Exception {
        doNothing().when(statsService).saveHit(any(EndpointHitDto.class));

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validHitDto)))
                .andExpect(status().isCreated());

        verify(statsService, times(1)).saveHit(any(EndpointHitDto.class));

    }

    @Test
    void saveHitShouldReturn400WhenAppIsBlank() throws Exception {
        validHitDto.setIp("");

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validHitDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).saveHit(any());
    }

    @Test
    void saveHitShouldReturn400WhenUriIsBlank() throws Exception {
        validHitDto.setUri("");

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validHitDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).saveHit(any());
    }

    @Test
    void saveHitShouldReturn400WhenIpIsBlank() throws Exception {
        validHitDto.setIp("");

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validHitDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).saveHit(any());
    }

    @Test
    void saveHitShouldReturn400WhenTimestampIsNull() throws Exception {
        validHitDto.setTimestamp(null);

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validHitDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).saveHit(any());
    }

    @Test
    void getStats_whenParamsAreValid_shouldReturn200Ok() throws Exception {
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now;
        List<String> uris = List.of("/test-uri");
        Boolean unique = false;

        when(statsService.getStats(eq(start), eq(end), eq(uris), eq(unique)))
                .thenReturn(List.of(new ViewStatsDto("test-app", "/test-uri", 10L)));

        mvc.perform(get("/stats")
                        .param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("uris", "/test-uri")
                        .param("unique", String.valueOf(unique)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        List.of(new ViewStatsDto("test-app", "/test-uri", 10L)))
                ));

        verify(statsService, times(1)).getStats(eq(start), eq(end), eq(uris), eq(unique));
    }

    @Test
    void getStats_whenNoUris_shouldReturn200Ok() throws Exception {
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now;
        Boolean unique = false;

        List<ViewStatsDto> statsList = List.of(new ViewStatsDto("test-app", "/", 5L));
        when(statsService.getStats(eq(start), eq(end), isNull(), eq(unique))).thenReturn(statsList);

        mvc.perform(get("/stats")
                        .param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("unique", String.valueOf(unique)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(statsList)));

        verify(statsService, times(1)).getStats(eq(start), eq(end), isNull(), eq(unique));
    }
}