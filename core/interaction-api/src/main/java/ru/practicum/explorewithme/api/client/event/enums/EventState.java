package ru.practicum.explorewithme.api.client.event.enums;

/**
 * Состояния жизненного цикла события
 */
public enum EventState {
    /**
     * Ожидает модерации
     */
    PENDING,

    /**
     * Опубликовано
     */
    PUBLISHED,

    /**
     * Отменено
     */
    CANCELED
}

