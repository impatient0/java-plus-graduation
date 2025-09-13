package ru.practicum.explorewithme.comment.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.api.client.comment.dto.CommentDto;
import ru.practicum.explorewithme.api.client.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.api.client.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.comment.application.CommentService;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Создание нового комментария {} зарегистрированным пользователем c id {} к событию с id {}",
                newCommentDto, userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(userId, eventId, newCommentDto));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Обновление комментария c id {} пользователем c id {}, новый комментарий {}",
                commentId, userId, updateCommentDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.updateUserComment(userId, commentId, updateCommentDto));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getUserComments(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentDto> result = commentService.getUserComments(userId, from, size);
        log.info("Получение списка комментариев {} пользователя c id {}", result, userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId) {
        log.info("User id={}: Received request to delete comment with Id: {}", userId, commentId);
        commentService.deleteUserComment(userId, commentId);
        log.info("User id={}: Comment with Id: {} marked as deleted", userId, commentId);
    }
}
