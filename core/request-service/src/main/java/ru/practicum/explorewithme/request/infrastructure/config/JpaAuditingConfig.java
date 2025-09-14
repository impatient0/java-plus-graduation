package ru.practicum.explorewithme.request.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@SuppressWarnings("unused")
public class JpaAuditingConfig {
}