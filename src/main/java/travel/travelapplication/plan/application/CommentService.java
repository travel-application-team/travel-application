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

  public void save(Comment comment) {
    commentRepository.save(comment);
  }

  public Comment findById(ObjectId id) {
    return commentRepository.findById(id).orElse(null);
  }

  public void saveReplyToComment(Comment comment, ReplyRequest replyRequest) {
    Reply reply = replyRequest.toEntity();
    replyRepository.insert(reply);

    comment.addReply(reply);
    save(comment);
  }
}
