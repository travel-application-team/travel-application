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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;

  public Comment findById(ObjectId id) {
    return commentRepository.findById(id).orElse(null);
  }

  public void saveReply(Comment comment, ReplyRequest replyRequest) {
    Reply reply = createNewReply(replyRequest);

    List<Reply> replies = comment.getReplies();
    replies.add(reply);

    commentRepository.save(comment);
  }

  private Reply createNewReply(ReplyRequest replyRequest) {
    Reply reply = Reply.builder()
        .content(replyRequest.getContent())
        .email(replyRequest.getEmail())
        .build();

    return replyRepository.insert(reply);
  }
}
