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
import travel.travelapplication.userplan.exception.UserPlanNotFoundException;
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
  private final MongoTemplate mongoTemplate;

  public UserPlan create(User user, UserPlanInfoRequest userPlanInfoRequest) {
    UserPlan userPlan = userPlanInfoRequest.toEntity();

    UserPlan savedUserPlan = userPlanRepository.save(userPlan);

    List<UserPlan> userPlans = user.getUserPlans();
    userPlans.add(savedUserPlan);

    if (isPublic(userPlan.getStatus())) {
      share(savedUserPlan, user);
    }

    if (savedUserPlan.getId() != null) {
      mongoTemplate.save(savedUserPlan);
    }

    return userPlan;
  }

  public UserPlan findById(ObjectId id) {
    return userPlanRepository.findById(id).orElseThrow(
        () -> new UserPlanNotFoundException(id));
  }

  public UserPlan updateUserPlanPlaces(ObjectId userPlanId, List<String> placeIds) {
    UserPlan userPlan = findById(userPlanId);

    List<Place> places = new ArrayList<>();
    placeIds.forEach(placeId -> {
      Place place = placeService.findByPlaceId(placeId);
      if (place != null) {
        places.add(place);
      } else {
        log.error("Place with id {} not found", placeId);
      }
    });

    userPlan.updatePlaces(places);
    userPlanRepository.save(userPlan);
    return userPlan;
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
      // 병합된 내용으로 lastUserPlan 업데이트
      lastUserPlan.updatePlaces(selectedPlaces);  // lastUserPlan을 직접 업데이트
      return userPlanRepository.save(lastUserPlan);  // 병합된 lastUserPlan 저장
    } else {
      return userPlanRepository.save(userPlan);
    }

  }

  public UserPlan updateUserPlanInfo(CustomOAuth2User oAuth2User, ObjectId userPlanId,
      UpdateUserPlanInfoRequest updateUserPlanInfoRequest) {
    UserPlan userPlan = findById(userPlanId);
    User user = userService.findUserByEmail(oAuth2User);
    UserPlan updatedUserPlan = updateNameAndStatus(userPlan, updateUserPlanInfoRequest);
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
    String newName = request.name() == null ? userPlan.getName() : request.name();
    Status newStatus = request.status() == null ? userPlan.getStatus() : request.status();

    userPlan.update(newName, newStatus);
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
