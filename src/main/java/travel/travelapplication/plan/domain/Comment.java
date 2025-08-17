package travel.travelapplication.plan.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Document(collection = "Comment")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private ObjectId id;

  private String content;
  private String email;

  @CreatedDate
  private Date createdAt;

  @DBRef
  private List<Reply> replies = new LinkedList<>();

  @PersistenceCreator
  public Comment() {
  }

  @PersistenceCreator
  @Builder
  public Comment(ObjectId id, String content, String email,
      Date createdAt, List<Reply> replies) {
    this.id = id;
    this.content = content;
    this.email = email;
    this.createdAt = createdAt;
    this.replies = replies;
  }

  public void addReply(Reply reply) {
    this.replies.add(reply);
  }
}
