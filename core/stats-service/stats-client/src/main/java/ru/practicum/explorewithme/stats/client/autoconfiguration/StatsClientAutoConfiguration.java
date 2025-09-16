package ru.practicum.explorewithme.stats.client.autoconfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import ru.practicum.explorewithme.stats.client.StatsClient;
import ru.practicum.explorewithme.stats.client.aop.StatsHitAspect;

@AutoConfiguration
@ConditionalOnBean(StatsClient.class)
public class StatsClientAutoConfiguration {

    @Bean
    public StatsHitAspect statsHitAspect(StatsClient statsClient) {
        return new StatsHitAspect(statsClient);
    }
}