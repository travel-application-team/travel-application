package travel.travelapplication.userplan.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.plan.domain.Route;

@Document("UserPlan")
@Getter
public class UserPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private ObjectId id;

  private String name;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  private Long budget;

  private String city;

  private String district; // 여행 지역 (여행 정보 입력)

  private Status status; // public, private

  @CreatedDate
  private Date createdAt;

  @LastModifiedDate
  private Date updatedAt;

  @DBRef
  private List<Place> places = new LinkedList<>();

  @DBRef
  private List<Route> routes = new ArrayList<>();


  @PersistenceCreator
  public UserPlan() {

  }

  @PersistenceCreator
  @Builder
  public UserPlan(String name, LocalDate startDate, LocalDate endDate, Long budget, String city,
      String district,
      Status status, List<Place> places, List<Route> routes) {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.budget = budget;
    this.city = city;
    this.district = district;
    this.status = status;
    this.places = places;
    this.routes = routes;
    this.createdAt = Date.from(Instant.now());
  }

  public void update(UserPlan userPlan) {
    Optional.ofNullable(userPlan.getName())
        .ifPresent(none -> this.name = userPlan.getName());
    Optional.ofNullable(userPlan.getStartDate())
        .ifPresent(none -> this.startDate = userPlan.getStartDate());
    Optional.ofNullable(userPlan.getEndDate())
        .ifPresent(none -> this.endDate = userPlan.getEndDate());
    Optional.ofNullable(userPlan.getBudget())
        .ifPresent(none -> this.budget = userPlan.getBudget());
    Optional.ofNullable(userPlan.getCity())
        .ifPresent(none -> this.city = userPlan.getCity());
    Optional.ofNullable(userPlan.getDistrict())
        .ifPresent(none -> this.district = userPlan.getDistrict());
    Optional.ofNullable(userPlan.getStatus())
        .ifPresent(none -> this.status = userPlan.getStatus());
    Optional.ofNullable(userPlan.getPlaces())
        .ifPresent(none -> this.places = userPlan.getPlaces());
    Optional.ofNullable(userPlan.getRoutes())
        .ifPresent(none -> this.routes = userPlan.getRoutes());
    this.updatedAt = Date.from(Instant.now());
  }

  public enum Status {
    PUBLIC("공개"), PRIVATE("비공개");

    private final String description;

    Status(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }
}

