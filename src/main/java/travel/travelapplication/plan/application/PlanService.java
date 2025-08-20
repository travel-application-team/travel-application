package travel.travelapplication.plan.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.plan.dto.PlanListItemResponse;
import travel.travelapplication.plan.dto.CommentRequest;
import travel.travelapplication.plan.domain.Comment;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.plan.dto.CommentResponse;
import travel.travelapplication.plan.dto.PlanResponse;
import travel.travelapplication.plan.dto.PlanSearchResponse;
import travel.travelapplication.plan.dto.ReplyResponse;
import travel.travelapplication.plan.repository.CommentRepository;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.plan.repository.PlanRepository;
import travel.travelapplication.userplan.dto.UserPlanInfoResponse;

@RequiredArgsConstructor
@Service
@Slf4j
public class PlanService {

  private final PlanRepository planRepository;
  private final CommentRepository commentRepository;
  private final UserService userService;

  public Plan findById(ObjectId id) {
    return planRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("not found:" + id));
  }

  public List<PlanListItemResponse> findAll() {
    List<Plan> plans = planRepository.findAll();

    return plans.stream()
        .map(PlanListItemResponse::fromEntity).toList();
  }

  public PlanResponse findPlan(ObjectId planId, User user) {
    Plan plan = findById(planId);
    UserPlan userPlan = plan.getUserPlan();

    UserPlanInfoResponse userPlanInfoResponse = UserPlanInfoResponse.fromEntity(userPlan);

    List<ObjectId> savedPlans = user.getSavedPlans().stream()
        .map(Plan::getId)
        .toList();

    List<CommentResponse> commentResponses = plan.getComments().stream()
        .map(comment -> {
          List<ReplyResponse> replies = comment.getReplies().stream()
              .map(ReplyResponse::fromEntity)
              .toList();
          return CommentResponse.fromEntity(comment, replies);
        }).toList();

    return PlanResponse.fromEntity(plan, userPlanInfoResponse, savedPlans, commentResponses);
  }

  public List<PlanSearchResponse> searchByPlace(String keyword) {
    List<Plan> plans = findByKeyword(keyword);

    return plans.stream()
        .map(PlanSearchResponse::fromEntity).toList();
  }

  private List<Plan> findByKeyword(String keyword) {
    if (keyword != null) {
      return planRepository.findByPlaceName(keyword);
    } else {
      return planRepository.findAll();
    }
  }

  public boolean toggleSavePlan(User user, ObjectId planId) throws IllegalAccessException {
    Plan plan = findById(planId);
    List<Plan> savedPlans = user.getSavedPlans();

    boolean isSaved = savedPlans.stream()
        .anyMatch(savedPlan -> savedPlan.getId().equals(plan.getId()));

    if (!isSaved) {
      savedPlans.add(plan);
    } else {
      savedPlans.removeIf(savedPlan -> savedPlan.getId().equals(plan.getId()));
    }
    userService.updateSavedPlans(user, savedPlans);
    return !isSaved;
  }

  public void saveCommentToPlan(Plan plan,
      CommentRequest commentRequest) {
    Comment comment = commentRequest.toEntity();
    commentRepository.save(comment);

    plan.addComment(comment);
    planRepository.save(plan);
  }

  public void updatePlanFromUserPlanInfo(Plan plan, UserPlan userPlan) {
    plan.updateUserPlan(userPlan);
    planRepository.save(plan);
  }
}
