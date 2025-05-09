package ru.practicum.explorewithme.stats.server.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;
import ru.practicum.explorewithme.stats.server.model.EndpointHit;

@DataJpaTest
@Testcontainers
@DisplayName("Stats Repository DataJpa Tests")
public class StatsRepositoryTest {

    // Настройка контейнера с тестовой БД
    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:16.1"));

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private StatsRepository statsRepository; // Тестируемый репозиторий

    @Autowired
    private TestEntityManager entityManager;

    // Тестовые данные для EndpointHit
    private EndpointHit hit1, hit2, hit3, hit4, hit5;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        hit1 = new EndpointHit(null, "app1", "/uri1", "192.168.0.1", now.minusHours(1));
        hit2 = new EndpointHit(null, "app1", "/uri1", "192.168.0.2", now.minusMinutes(30));
        hit3 = new EndpointHit(null, "app1", "/uri1", "192.168.0.1", now.minusMinutes(10)); // Повторный IP для /uri1
        hit4 = new EndpointHit(null, "app2", "/uri2", "192.168.0.3", now.minusHours(2));
        hit5 = new EndpointHit(null, "app1", "/uri3", "192.168.0.1", now.minusMinutes(5)); // Другой URI, но IP как у hit1

        statsRepository.saveAll(List.of(hit1, hit2, hit3, hit4, hit5));
    }

    @AfterEach
    void tearDown() {
        statsRepository.deleteAll();
    }

    @Nested
    @DisplayName("findStats (статистика по общему количеству хитов)")
    class FindStatsTest {

        @Test
        @DisplayName("Должен вернуть корректное общее количество хитов для указанных URI в заданном временном диапазоне")
        void findStats_whenUrisProvided_shouldReturnCorrectStats() {
            LocalDateTime start = now.minusHours(3);
            LocalDateTime end = now.plusHours(1);
            List<String> uris = List.of("/uri1", "/uri3");

            List<ViewStatsDto> result = statsRepository.findStats(start, end, uris);

            assertThat(result).hasSize(2); // Ожидаем статистику для двух URI: /uri1 и /uri3

            // Проверка статистики для /uri1
            ViewStatsDto statsUri1 = result.stream().filter(s -> s.getUri().equals("/uri1")).findFirst().orElse(null);
            assertThat(statsUri1).isNotNull();
            assertThat(statsUri1.getApp()).isEqualTo("app1");
            assertThat(statsUri1.getHits()).isEqualTo(3L); // hit1, hit2, hit3 для /uri1

            // Проверка статистики для /uri3
            ViewStatsDto statsUri3 = result.stream().filter(s -> s.getUri().equals("/uri3")).findFirst().orElse(null);
            assertThat(statsUri3).isNotNull();
            assertThat(statsUri3.getApp()).isEqualTo("app1");
            assertThat(statsUri3.getHits()).isEqualTo(1L); // hit5 для /uri3
        }

        @Test
        @DisplayName("Должен вернуть корректное общее количество хитов для всех URI в заданном временном диапазоне, если URI не указаны")
        void findStats_whenUrisNotProvided_shouldReturnStatsForAllUris() {
            LocalDateTime start = now.minusHours(3);
            LocalDateTime end = now.plusHours(1);

            List<ViewStatsDto> result = statsRepository.findStats(start, end, null); // URI не указаны

            assertThat(result).hasSize(3); // Ожидаем статистику для трех уникальных URI: /uri1, /uri2, /uri3

            // Результат отсортирован по app, затем по URI, затем по количеству хитов (здесь /uri1 первый)
            assertThat(result.getFirst().getUri()).isEqualTo("/uri1"); // /uri1 имеет 3 хита
            assertThat(result.getFirst().getHits()).isEqualTo(3L);

            // Проверка наличия и корректности данных для /uri2 и /uri3
            boolean foundUri2 = result.stream().anyMatch(s -> s.getUri().equals("/uri2") && s.getHits() == 1L); // hit4 для /uri2
            boolean foundUri3 = result.stream().anyMatch(s -> s.getUri().equals("/uri3") && s.getHits() == 1L); // hit5 для /uri3
            assertThat(foundUri2).isTrue();
            assertThat(foundUri3).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если временной диапазон не содержит хитов")
        void findStats_whenTimeRangeExcludesData_shouldReturnEmptyList() {
            // Задаем временной диапазон, который гарантированно не содержит тестовых данных
            LocalDateTime start = now.plusHours(1);
            LocalDateTime end = now.plusHours(2);

            List<ViewStatsDto> result = statsRepository.findStats(start, end, null);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUniqueStats (статистика по уникальным IP)")
    class FindUniqueStatsTest {

        @Test
        @DisplayName("Должен вернуть корректное количество уникальных хитов (по IP) для указанных URI в заданном временном диапазоне")
        void findUniqueStats_whenUrisProvided_shouldReturnCorrectUniqueStats() {
            LocalDateTime start = now.minusHours(3);
            LocalDateTime end = now.plusHours(1);
            List<String> uris = List.of("/uri1"); // Только для /uri1

            List<ViewStatsDto> result = statsRepository.findUniqueStats(start, end, uris);

            assertThat(result).hasSize(1); // Ожидаем статистику только для /uri1
            ViewStatsDto statsUri1 = result.getFirst();
            assertThat(statsUri1.getApp()).isEqualTo("app1");
            assertThat(statsUri1.getUri()).isEqualTo("/uri1");
            assertThat(statsUri1.getHits()).isEqualTo(2L); // Уникальные IP для /uri1: 192.168.0.1, 192.168.0.2
        }

        @Test
        @DisplayName("Должен вернуть корректное количество уникальных хитов (по IP) для всех URI в заданном временном диапазоне, если URI не указаны")
        void findUniqueStats_whenUrisNotProvided_shouldReturnUniqueStatsForAllUris() {
            LocalDateTime start = now.minusHours(3);
            LocalDateTime end = now.plusHours(1);

            List<ViewStatsDto> result = statsRepository.findUniqueStats(start, end, null); // URI не указаны

            assertThat(result).hasSize(3); // Ожидаем статистику для трех уникальных URI

            // Проверка статистики для /uri1
            ViewStatsDto statsUri1 = result.stream().filter(s -> s.getUri().equals("/uri1")).findFirst().orElse(null);
            assertThat(statsUri1).isNotNull();
            assertThat(statsUri1.getApp()).isEqualTo("app1");
            assertThat(statsUri1.getHits()).isEqualTo(2L); // Уникальные IP для /uri1: 192.168.0.1, 192.168.0.2

            // Проверка статистики для /uri2
            ViewStatsDto statsUri2 = result.stream().filter(s -> s.getUri().equals("/uri2")).findFirst().orElse(null);
            assertThat(statsUri2).isNotNull();
            assertThat(statsUri2.getApp()).isEqualTo("app2");
            assertThat(statsUri2.getHits()).isEqualTo(1L); // Уникальный IP для /uri2: 192.168.0.3

            // Проверка статистики для /uri3
            ViewStatsDto statsUri3 = result.stream().filter(s -> s.getUri().equals("/uri3")).findFirst().orElse(null);
            assertThat(statsUri3).isNotNull();
            assertThat(statsUri3.getApp()).isEqualTo("app1");
            assertThat(statsUri3.getHits()).isEqualTo(1L); // Уникальный IP для /uri3: 192.168.0.1
        }
    }
}