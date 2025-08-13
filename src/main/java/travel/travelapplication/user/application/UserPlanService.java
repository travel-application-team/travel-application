package travel.travelapplication.user.application;

import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import travel.travelapplication.constant.Status;
import travel.travelapplication.dto.userplan.UpdateUserPlanInfoRequest;
import travel.travelapplication.dto.userplan.UserPlanInfoResponse;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.plan.application.PlanService;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.plan.repository.PlanRepository;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.domain.UserPlan;
import travel.travelapplication.user.repository.UserPlanRepository;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPlanService {

    private final UserService userService;
    private MongoTemplate mongoTemplate;
    private final UserPlanRepository userPlanRepository;
    private final PlanRepository planRepository;
    private final PlanService planService;

    public UserPlan createNewUserPlan(User user, UserPlanInfoResponse userPlanInfoResponse) {
        UserPlan userPlan = userPlanInfoResponse.toEntity();

        UserPlan savedUserPlan = userPlanRepository.insert(userPlan);

        List<UserPlan> userPlans = user.getUserPlans();
        userPlans.add(savedUserPlan);

        user.update(user);
        userService.save(user);

        if (isPublic(userPlan.getStatus())) {
            shareUserPlan(savedUserPlan, user);
        }

        if (savedUserPlan.getId() != null) {
            mongoTemplate.save(savedUserPlan);
        }

        return userPlan;
    }


    public List<UserPlan> findAllUserPlan(User user) {
        return userPlanRepository.findAll();
    }

    public UserPlan findUserPlanById(ObjectId userPlanId) throws IllegalAccessException {

        UserPlan userPlan = userPlanRepository.findById(userPlanId)
                .orElse(null);

        if (userPlan != null) {
            log.info("user plan found");
            return userPlan;
        } else {
            throw new IllegalAccessException("user plan not found");
        }
    }


    public void shareUserPlan(UserPlan userPlan, User user) { // 공개 처리 -> 커뮤니티 공유
        Plan existingPlan = planRepository.findByUserPlan(userPlan);

        if (existingPlan != null) {
            planService.updatePlanFromUserPlanInfo(existingPlan, userPlan);
        } else {
            Plan plan = new Plan(userPlan.getName(), userPlan, user.getEmail(), new LinkedList<>());
            planRepository.insert(plan);
        }
    }

    private boolean isPublic(Status status) {
        return status.equals(Status.PUBLIC);
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
        save(userPlan);
    }

    public void mergePlacesToUserPlanInfo(UserPlan userPlan, List<Place> places) {
        // 가장 최근 UserPlan 도큐먼트 조회
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "_id")).limit(1);
        UserPlan lastUserPlan = mongoTemplate.findOne(query, UserPlan.class);

        if (lastUserPlan != null) {
            UserPlan updatedUserPlan = UserPlan.builder()
                    .name(userPlan.getName() != null ? userPlan.getName() : lastUserPlan.getName())
                    .startDate(userPlan.getStartDate() != null ? userPlan.getStartDate() : lastUserPlan.getStartDate())
                    .endDate(userPlan.getEndDate() != null ? userPlan.getEndDate() : lastUserPlan.getEndDate())
                    .budget(userPlan.getBudget() != null ? userPlan.getBudget() : lastUserPlan.getBudget())
                    .city(userPlan.getCity() != null ? userPlan.getCity() : lastUserPlan.getCity())
                    .district(userPlan.getDistrict() != null ? userPlan.getDistrict() : lastUserPlan.getDistrict())
                    .status(userPlan.getStatus() != null ? userPlan.getStatus() : lastUserPlan.getStatus())
                    .places(places != null ? places : lastUserPlan.getPlaces()) // 새로운 places로 덮어씀
                    .routes(userPlan.getRoutes() != null ? userPlan.getRoutes() : lastUserPlan.getRoutes())
                    .build();

            // 병합된 내용으로 lastUserPlan 업데이트
            lastUserPlan.update(updatedUserPlan);  // lastUserPlan을 직접 업데이트
            save(lastUserPlan);  // 병합된 lastUserPlan 저장
            log.info("update successful");

        } else {
            save(userPlan);
        }

    }

    public void save(UserPlan userPlan) {
        userPlanRepository.save(userPlan);
    }

    public void updateUserPlanInfo(User user, UserPlan userPlan, UpdateUserPlanInfoRequest userPlanInfoDto) {
        UserPlan updatedUserPlan = updateNameAndStatus(userPlan, userPlanInfoDto);

        user.update(user);
        userService.save(user);

        if (isPublic(userPlan.getStatus())) {
            shareUserPlan(updatedUserPlan, user); // 만약 공개로 그대로 두고, 이름을 게속 바꾸면 이름만 다른 동일한 일정이 계속 저장되는 문제 발생
        } else {
            removePlanByUserPlan(updatedUserPlan);
        }
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
        save(userPlan);
        return userPlan;
    }

    private void removePlanByUserPlan(UserPlan userPlan) {
        Plan planToRemove = planRepository.findByUserPlan(userPlan);
        if (planToRemove != null) {
            planRepository.delete(planToRemove);
        }
    }
}
