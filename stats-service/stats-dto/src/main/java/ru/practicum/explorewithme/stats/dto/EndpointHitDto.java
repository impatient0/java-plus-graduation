package ru.practicum.explorewithme.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    private Long id;

    @NotNull(message = "Поле app не может быть пустым")
    private String app;

    @NotNull(message = "Поле uri не может быть пустым")
    private String uri;

    @NotNull(message = "Поле ip не может быть пустым")
    private String ip;

    @NotNull(message = "Поле timestamp не может быть пустым")
    @PastOrPresent(message = "Поле timestamp должно быть не позже текущей даты и времени")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}