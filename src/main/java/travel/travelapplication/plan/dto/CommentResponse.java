package travel.travelapplication.plan.dto;

import java.util.List;
import travel.travelapplication.plan.domain.Comment;

public record CommentResponse(
    String content,
    String email,
    List<ReplyResponse> replies
) {

  public static CommentResponse fromEntity(Comment comment, List<ReplyResponse> replies) {
    return new CommentResponse(comment.getContent(), comment.getEmail(), replies);
  }
}
