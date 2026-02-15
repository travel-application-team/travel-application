package travel.travelapplication.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import travel.travelapplication.place.domain.Tag;
import travel.travelapplication.plan.domain.Plan;

import java.util.ArrayList;
import java.util.List;
import travel.travelapplication.userplan.domain.UserPlan;

@Document(collection = "User")
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private ObjectId id;

  private String name;

  private String email;

  private String password;

  private String role;

  @DBRef
  private List<UserPlan> userPlans = new ArrayList<>();

  @DBRef
  private List<Tag> tags = new ArrayList<>();

  private List<Long> likedPlaces = new ArrayList<>();

  @DBRef
  private List<Plan> savedPlans = new ArrayList<>();

  private String refreshToken;

  private String accessToken;

  @PersistenceCreator
  public User() {
  }

  @PersistenceCreator
  @Builder
  public User(String name, String email, String password, List<UserPlan> userPlans, List<Tag> tags,
      List<Long> likedPlaces, List<Plan> savedPlans, String role, String accessToken) {
    this.name = name;
    this.email = email;
    this.password=password;
    this.userPlans = userPlans;
    this.tags = tags;
    this.likedPlaces = likedPlaces;
    this.savedPlans = savedPlans;
    this.role = role;
    this.accessToken = accessToken;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateTags(List<Tag> tags) {
    this.tags = tags;
  }

  public void updateSavedPlans(List<Plan> savedPlans) {
    this.savedPlans = savedPlans;
  }

  public void updateLikedPlaces(List<Long> likedPlaces) {
    this.likedPlaces = likedPlaces;
  }

}
