package travel.travelapplication.plan.dto;

import travel.travelapplication.plan.domain.Comment;

import java.util.LinkedList;

public record CommentRequest(
    String content,
    String email
) {

  public Comment toEntity() {
    return Comment.builder()
        .content(content)
        .email(email)
        .replies(new LinkedList<>())
        .build();

  }
}
