package ru.practicum.explorewithme.stats.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EndpointHitDtoTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testSerializationToJson() throws Exception {
        // Подготовка тестовых данных
        LocalDateTime timestamp = LocalDateTime.of(2024, 3, 15, 12, 30, 0);
        EndpointHitDto dto = new EndpointHitDto(
                "test-app",
                "/test/path",
                "192.168.1.1",
                timestamp
        );

        // Сериализация в JSON
        String json = objectMapper.writeValueAsString(dto);

        // Проверки
        assertTrue(json.contains("\"app\":\"test-app\""));
        assertTrue(json.contains("\"uri\":\"/test/path\""));
        assertTrue(json.contains("\"ip\":\"192.168.1.1\""));
        assertTrue(json.contains("\"timestamp\":\"2024-03-15 12:30:00\""));
    }

    @Test
    void testDeserializationFromJson() throws Exception {
        // Подготовка JSON
        String json = """
                {
                    "app": "test-app",
                    "uri": "/test/path",
                    "ip": "192.168.1.1",
                    "timestamp": "2024-03-15 12:30:00"
                }""";

        // Десериализация из JSON
        EndpointHitDto dto = objectMapper.readValue(json, EndpointHitDto.class);

        // Проверки
        assertEquals("test-app", dto.getApp());
        assertEquals("/test/path", dto.getUri());
        assertEquals("192.168.1.1", dto.getIp());
        assertEquals(
                LocalDateTime.of(2024, 3, 15, 12, 30, 0),
                dto.getTimestamp()
        );
    }

    @Test
    void testInvalidTimestampFormat() {
        // Подготовка JSON с неверным форматом даты
        String json = """
                {
                    "id": 1,
                    "app": "test-app",
                    "uri": "/test/path",
                    "ip": "192.168.1.1",
                    "timestamp": "2024-03-15T12:30:00"
                }""";

        // Проверка исключения при неверном формате
        assertThrows(Exception.class, () ->
                objectMapper.readValue(json, EndpointHitDto.class)
        );
    }

    @Test
    void testNullValues() throws Exception {
        // Создание объекта с null-значениями
        EndpointHitDto dto = new EndpointHitDto(null, null, null, null);

        // Сериализация
        String json = objectMapper.writeValueAsString(dto);

        // Десериализация
        EndpointHitDto deserializedDto = objectMapper.readValue(json, EndpointHitDto.class);

        // Проверки
        assertNull(deserializedDto.getApp());
        assertNull(deserializedDto.getUri());
        assertNull(deserializedDto.getIp());
        assertNull(deserializedDto.getTimestamp());
    }
}