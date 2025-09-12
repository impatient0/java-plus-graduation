package ru.practicum.explorewithme.api.exception;

public class EntityDeletedException  extends RuntimeException {

    public EntityDeletedException(String message) {
        super(message);
    }

    public EntityDeletedException(String entityName, String fieldName, Object value) {
        super(String.format("Entity restriction of removal %s with %s = '%s' - not empty", entityName, fieldName, value));
    }
}
