package ru.practicum.ewm.analyzer.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionConsumer {

    @KafkaListener(
        id = "action-listener",
        containerFactory = "actionContainerFactory",
        topics = "${kafka.topic.user-actions}"
    )
    public void consumeAction(UserActionAvro action) {
        log.info("Received action from user {} on event {}", action.getUserId(), action.getEventId());
    }

}
