package ru.practicum.explorewithme.main.service;

import com.querydsl.core.BooleanBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.api.client.comment.dto.CommentAdminDto;
import ru.practicum.explorewithme.api.client.comment.dto.CommentDto;
import ru.practicum.explorewithme.api.client.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.api.client.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.api.client.user.dto.UserDto;
import ru.practicum.explorewithme.api.error.BusinessRuleViolationException;
import ru.practicum.explorewithme.api.error.EntityNotFoundException;
import ru.practicum.explorewithme.api.client.user.UserClient;
import ru.practicum.explorewithme.main.mapper.CommentMapper;
import ru.practicum.explorewithme.main.mapper.DtoMapper;
import ru.practicum.explorewithme.main.model.Comment;
import ru.practicum.explorewithme.main.model.Event;
import ru.practicum.explorewithme.main.model.EventState;
import ru.practicum.explorewithme.main.model.QComment;
import ru.practicum.explorewithme.main.repository.CommentRepository;
import ru.practicum.explorewithme.main.repository.EventRepository;
import ru.practicum.explorewithme.main.service.params.AdminCommentSearchParams;
import ru.practicum.explorewithme.main.service.params.PublicCommentParameters;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserClient userClient;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final DtoMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsForEvent(Long eventId, PublicCommentParameters parameters) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException("Published event", "Id", eventId));

        if (!event.isCommentsEnabled()) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(parameters.getFrom() / parameters.getSize(),
                parameters.getSize(), parameters.getSort());

        List<Comment> result = commentRepository.findByEventIdAndIsDeletedFalse(eventId, pageable).getContent();

        return commentMapper.toDtoList(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        userClient.checkUserExists(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        Pageable pageable = PageRequest.of(from / size, size, sort);

        List<Comment> result = commentRepository.findByAuthorIdAndIsDeletedFalse(userId, pageable).getContent();

        return commentMapper.toDtoList(result);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        UserDto author = userClient.getUserById(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с id " + eventId + " не найдено"));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BusinessRuleViolationException("Событие еще не опубликовано");
        }

        if (!event.isCommentsEnabled()) {
            throw new BusinessRuleViolationException("Комментарии запрещены");
        }

        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setEvent(event);
        comment.setAuthorId(userId);

        CommentDto savedComment = commentMapper.toDto(commentRepository.save(comment));
        savedComment.setAuthor(dtoMapper.toUserShortDto(author));

        return savedComment;
    }

    @Override
    @Transactional
    public CommentDto updateUserComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        Optional<Comment> comment = commentRepository.findById(commentId);

        if (comment.isEmpty()) {
            throw new EntityNotFoundException("Комментарий с id " + commentId + " не найден");
        }

        Comment existedComment = comment.get();

        if (!existedComment.getAuthorId().equals(userId)) {
            throw new EntityNotFoundException("Искомый комментарий с id " + commentId + " пользователя с id " + userId + " не найден");
        }

        if (existedComment.isDeleted()) {
            throw new BusinessRuleViolationException("Редактирование невозможно. Комментарий удален");
        }

        if (existedComment.getCreatedOn().isBefore(LocalDateTime.now().minusHours(6))) {
            throw new BusinessRuleViolationException("Время для редактирования истекло");
        }

        existedComment.setText(updateCommentDto.getText());
        existedComment.setEdited(true);

        return commentMapper.toDto(commentRepository.saveAndFlush(existedComment));
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d not found", commentId)));
        if (!comment.isDeleted()) {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
    }

    @Override
    @Transactional
    public void deleteUserComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d not found", commentId)));
        if (!comment.getAuthorId().equals(userId)) {
            throw new EntityNotFoundException(String.format("Comment with id=%d not found for user with id=%d", commentId, userId));
        }
        if (!comment.isDeleted()) {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
    }

    @Override
    @Transactional
    public CommentAdminDto restoreCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d not found", commentId)));
        if (comment.isDeleted()) {
            comment.setDeleted(false);
            commentRepository.save(comment);
        }
        return commentMapper.toAdminDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentAdminDto> getAllCommentsAdmin(AdminCommentSearchParams searchParams, int from, int size) {
        log.debug("Admin: Searching all comments with params: {}, from={}, size={}", searchParams, from, size);

        QComment qComment = QComment.comment;
        BooleanBuilder predicate = new BooleanBuilder();

        if (searchParams.getUserId() != null) {
            predicate.and(qComment.authorId.eq(searchParams.getUserId()));
        }

        if (searchParams.getEventId() != null) {
            predicate.and(qComment.event.id.eq(searchParams.getEventId()));
        }

        if (searchParams.getIsDeleted() != null) {
            predicate.and(qComment.isDeleted.eq(searchParams.getIsDeleted()));
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "createdOn"));

        Page<Comment> commentPage = commentRepository.findAll(predicate, pageable);

        List<CommentAdminDto> result = commentMapper.toAdminDtoList(commentPage.getContent());
        log.debug("Admin: Found {} comments for the given criteria.", result.size());
        return result;
    }
}
