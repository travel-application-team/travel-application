package travel.travelapplication.plan.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.plan.dto.ReplyRequest;
import travel.travelapplication.plan.domain.Comment;
import travel.travelapplication.plan.domain.Reply;
import travel.travelapplication.plan.repository.CommentRepository;
import travel.travelapplication.plan.repository.ReplyRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;

  public Comment findById(ObjectId id) {
    return commentRepository.findById(id).orElse(null);
  }

  public void saveReplyToComment(ObjectId commentId, ReplyRequest replyRequest) {
    Comment comment = findById(commentId);
    Reply reply = replyRequest.toEntity();
    replyRepository.save(reply);

    comment.addReply(reply);
    commentRepository.save(comment);
  }
}
