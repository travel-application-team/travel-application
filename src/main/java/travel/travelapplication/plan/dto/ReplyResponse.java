package travel.travelapplication.plan.dto;

import travel.travelapplication.plan.domain.Reply;

public record ReplyResponse(
    String content,
    String email
) {

  public static ReplyResponse fromEntity(Reply reply) {
    return new ReplyResponse(reply.getContent(), reply.getEmail());
  }
}
