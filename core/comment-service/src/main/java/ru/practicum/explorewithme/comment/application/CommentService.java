package ru.practicum.explorewithme.comment.application;

import java.util.List;
import ru.practicum.explorewithme.api.client.comment.dto.CommentAdminDto;
import ru.practicum.explorewithme.api.client.comment.dto.CommentDto;
import ru.practicum.explorewithme.api.client.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.api.client.comment.dto.UpdateCommentDto;

public interface CommentService {

    List<CommentDto> getCommentsForEvent(Long eventId, PublicCommentParameters publicCommentParameters);

    List<CommentDto> getUserComments(Long userId, int from, int size);

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateUserComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteCommentByAdmin(Long commentId);

    void deleteUserComment(Long userId, Long commentId);

    CommentAdminDto restoreCommentByAdmin(Long commentId);

    List<CommentAdminDto> getAllCommentsAdmin(AdminCommentSearchParams searchParams, int from, int size);
}