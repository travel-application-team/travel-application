package travel.travelapplication.plan.dto;

import java.util.List;

public record CommentResponse(
    String content,
    String email,
    List<ReplyResponse> replies
) {

}
