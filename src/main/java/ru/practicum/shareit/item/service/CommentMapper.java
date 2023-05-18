package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment toCommentModel(CommentDto commentDto) {
        Comment result = new Comment();
        result.setId(commentDto.getId());
        result.setText(commentDto.getText());
        result.setCreated(commentDto.getCreated());
        return result;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto result = new CommentDto();
        result.setId(comment.getId());
        result.setText(comment.getText());
        result.setAuthorName(comment.getAuthor().getName());
        result.setCreated(comment.getCreated());
        return result;
    }
}

