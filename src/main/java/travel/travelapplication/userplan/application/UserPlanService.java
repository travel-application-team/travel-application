package travel.travelapplication.userplan.application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.place.application.PlaceService;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.plan.application.PlanService;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.plan.repository.PlanRepository;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.userplan.domain.UserPlan.Status;
import travel.travelapplication.userplan.dto.UpdateUserPlanInfoRequest;
import travel.travelapplication.userplan.dto.UserPlanInfoRequest;
import travel.travelapplication.userplan.repository.UserPlanRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPlanService {

  private final UserService userService;
  private final UserPlanRepository userPlanRepository;
  private final PlanRepository planRepository;
  private final PlanService planService;
  private final PlaceService placeService;
  private MongoTemplate mongoTemplate;

  public UserPlan create(User user, UserPlanInfoRequest userPlanInfoRequest) {
    UserPlan userPlan = userPlanInfoRequest.toEntity();

    UserPlan savedUserPlan = userPlanRepository.save(userPlan);

    List<UserPlan> userPlans = user.getUserPlans();
    userPlans.add(savedUserPlan);

    user.update(user);
    userService.save(user);

    if (isPublic(userPlan.getStatus())) {
      share(savedUserPlan, user);
    }

    if (savedUserPlan.getId() != null) {
      mongoTemplate.save(savedUserPlan);
    }

    return userPlan;
  }

  public UserPlan findById(ObjectId userPlanId) throws IllegalAccessException {
    return userPlanRepository.findById(userPlanId).orElseThrow(
        () -> new IllegalAccessException("UserPlan not found with id: " + userPlanId));
  }

  public void updateUserPlanPlaces(UserPlan userPlan, List<Place> places) {
    UserPlan updatedUserPlan = UserPlan.builder()
        .name(userPlan.getName())
        .startDate(userPlan.getStartDate())
        .endDate(userPlan.getEndDate())
        .budget(userPlan.getBudget())
        .city(userPlan.getCity())
        .district(userPlan.getDistrict())
        .status(userPlan.getStatus())
        .places(places)
        .routes(userPlan.getRoutes())
        .build();

    userPlan.update(updatedUserPlan);
    userPlanRepository.save(userPlan);
  }

  // TODO: 더 안전한 방법으로 로직 개선 필요 - 고민중
  public UserPlan mergePlacesToUserPlanInfo(List<String> placeIds) {

    UserPlan userPlan = new UserPlan();
    List<Place> selectedPlaces = new ArrayList<>();
    for (String placeId : placeIds) {
      Place place = placeService.findByPlaceId(placeId);
      selectedPlaces.add(place);
    }

    // 가장 최근 UserPlan 도큐먼트 조회
    Query query = new Query().with(Sort.by(Direction.DESC, "_id")).limit(1);
    UserPlan lastUserPlan = mongoTemplate.findOne(query, UserPlan.class);

    if (lastUserPlan != null) {
      UserPlan updatedUserPlan = UserPlan.builder()
          .name(userPlan.getName() != null ? userPlan.getName() : lastUserPlan.getName())
          .startDate(userPlan.getStartDate() != null ? userPlan.getStartDate()
              : lastUserPlan.getStartDate())
          .endDate(
              userPlan.getEndDate() != null ? userPlan.getEndDate() : lastUserPlan.getEndDate())
          .budget(userPlan.getBudget() != null ? userPlan.getBudget() : lastUserPlan.getBudget())
          .city(userPlan.getCity() != null ? userPlan.getCity() : lastUserPlan.getCity())
          .district(
              userPlan.getDistrict() != null ? userPlan.getDistrict() : lastUserPlan.getDistrict())
          .status(userPlan.getStatus() != null ? userPlan.getStatus() : lastUserPlan.getStatus())
          .places(selectedPlaces)
          .routes(userPlan.getRoutes() != null ? userPlan.getRoutes() : lastUserPlan.getRoutes())
          .build();

      // 병합된 내용으로 lastUserPlan 업데이트
      lastUserPlan.update(updatedUserPlan);  // lastUserPlan을 직접 업데이트
      return userPlanRepository.save(lastUserPlan);  // 병합된 lastUserPlan 저장
    } else {
      return userPlanRepository.save(userPlan);
    }

  }

  public UserPlan updateUserPlanInfo(CustomOAuth2User oAuth2User, ObjectId userPlanId,
      UpdateUserPlanInfoRequest updateUserPlanInfoRequest) throws IllegalAccessException {
    UserPlan userPlan = findById(userPlanId);
    User user = userService.findUserByEmail(oAuth2User);
    UserPlan updatedUserPlan = updateNameAndStatus(userPlan, updateUserPlanInfoRequest);

    user.update(user);
    userService.save(user);

    if (isPublic(userPlan.getStatus())) {
      share(updatedUserPlan, user); // 만약 공개로 그대로 두고, 이름을 게속 바꾸면 이름만 다른 동일한 일정이 계속 저장되는 문제 발생
    } else {
      removePlanByUserPlan(updatedUserPlan);
    }
    return updatedUserPlan;
  }

  private void share(UserPlan userPlan, User user) {
    // 공개 처리 -> 커뮤니티 공유
    Plan existingPlan = planRepository.findByUserPlan(userPlan);

    if (existingPlan != null) {
      planService.updatePlanFromUserPlanInfo(existingPlan, userPlan);
    } else {
      Plan plan = new Plan(userPlan.getName(), userPlan, user.getEmail(), new LinkedList<>());
      planRepository.save(plan);
    }
  }

  private boolean isPublic(Status status) {
    return status.equals(Status.PUBLIC);
  }

  private UserPlan updateNameAndStatus(UserPlan userPlan, UpdateUserPlanInfoRequest request) {
    UserPlan updatedUserPlan = UserPlan.builder()
        .name(request.name())
        .startDate(userPlan.getStartDate())
        .endDate(userPlan.getEndDate())
        .budget(userPlan.getBudget())
        .city(userPlan.getCity())
        .district(userPlan.getDistrict())
        .status(request.status())
        .places(userPlan.getPlaces())
        .routes(userPlan.getRoutes())
        .build();

    userPlan.update(updatedUserPlan);
    userPlanRepository.save(userPlan);
    return userPlan;
  }

  private void removePlanByUserPlan(UserPlan userPlan) {
    Plan planToRemove = planRepository.findByUserPlan(userPlan);
    if (planToRemove != null) {
      planRepository.delete(planToRemove);
    }
  }
}
