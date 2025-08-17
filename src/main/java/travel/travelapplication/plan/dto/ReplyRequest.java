package travel.travelapplication.plan.dto;

import travel.travelapplication.plan.domain.Reply;

public record ReplyRequest(
    String content,
    String email
) {

  public Reply toEntity() {
    return Reply.builder()
        .content(content)
        .email(email)
        .build();
  }
}
