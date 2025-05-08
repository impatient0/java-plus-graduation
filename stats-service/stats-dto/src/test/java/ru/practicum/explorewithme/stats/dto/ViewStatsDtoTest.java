package ru.practicum.explorewithme.stats.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewStatsDtoTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSerializationToJson() throws Exception {
        // Подготовка тестовых данных
        ViewStatsDto dto = new ViewStatsDto();
        dto.setApp("test-service");
        dto.setUri("/events/1");
        dto.setHits(100L);

        // Сериализация в JSON
        String json = objectMapper.writeValueAsString(dto);

        // Проверки
        assertTrue(json.contains("\"app\":\"test-service\""));
        assertTrue(json.contains("\"uri\":\"/events/1\""));
        assertTrue(json.contains("\"hits\":100"));
    }

    @Test
    void testDeserializationFromJson() throws Exception {
        // Подготовка JSON
        String json = """
                {
                    "app": "test-service",
                    "uri": "/events/1",
                    "hits": 100
                }""";

        // Десериализация из JSON
        ViewStatsDto dto = objectMapper.readValue(json, ViewStatsDto.class);

        // Проверки
        assertEquals("test-service", dto.getApp());
        assertEquals("/events/1", dto.getUri());
        assertEquals(100L, dto.getHits());
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        // JSON с отсутствующими полями
        String json = """
                {
                    "app": "test-service"
                }""";

        ViewStatsDto dto = objectMapper.readValue(json, ViewStatsDto.class);

        // Проверки
        assertEquals("test-service", dto.getApp());
        assertNull(dto.getUri());
        assertNull(dto.getHits());
    }

    @Test
    void testNullValues() throws Exception {
        // Создание объекта с null-значениями
        ViewStatsDto dto = new ViewStatsDto();

        // Сериализация
        String json = objectMapper.writeValueAsString(dto);

        // Десериализация
        ViewStatsDto deserializedDto = objectMapper.readValue(json, ViewStatsDto.class);

        // Проверки
        assertNull(deserializedDto.getApp());
        assertNull(deserializedDto.getUri());
        assertNull(deserializedDto.getHits());
    }
}