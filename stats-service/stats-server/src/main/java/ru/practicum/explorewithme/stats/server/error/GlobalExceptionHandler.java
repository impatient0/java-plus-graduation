package ru.practicum.explorewithme.stats.server.error;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.common.error.ApiError;

@RestControllerAdvice
@Slf4j
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        String errorMessage = "Validation error(s): " + String.join("; ", errors);
        log.warn(errorMessage, e);
        return ApiError.builder()
            .errors(errors)
            .status(HttpStatus.BAD_REQUEST)
            .reason("Incorrectly made request due to validation errors.")
            .message(errorMessage)
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameter(final MissingServletRequestParameterException e) {
        String errorMessage = "Required request parameter is not present: " + e.getParameterName();
        log.warn(errorMessage, e);
        return ApiError.builder()
            .status(HttpStatus.BAD_REQUEST)
            .reason("Incorrectly made request.")
            .message(errorMessage)
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage(), e);
        return ApiError.builder()
            .status(HttpStatus.BAD_REQUEST)
            .reason("Incorrectly made request due to an invalid argument.")
            .message(e.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        return ApiError.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .reason("An unexpected error occurred on the server.")
            .message("An internal server error has occurred: " + e.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }
}
