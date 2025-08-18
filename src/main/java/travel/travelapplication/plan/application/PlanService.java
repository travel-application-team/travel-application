package travel.travelapplication.plan.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.plan.dto.CommentRequest;
import travel.travelapplication.plan.domain.Comment;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.plan.repository.CommentRepository;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.plan.repository.PlanRepository;

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

  public boolean toggleSavePlan(User user, Plan plan) {
    List<Plan> savedPlans = user.getSavedPlans();
    boolean isSaved = savedPlans.stream()
        .anyMatch(savedPlan -> savedPlan.getId().equals(plan.getId()));

    if (!isSaved) {
      savedPlans.add(plan);
    } else {
      savedPlans.removeIf(savedPlan -> savedPlan.getId().equals(plan.getId()));
    }
    userService.updateUserSavedPlans(user, savedPlans);
    return !isSaved;
  }

  public void saveCommentToPlan(Plan plan,
      CommentRequest commentRequest) { // 커뮤니티 Plan 댓글 저장 기능 (답글 제외)
    Comment comment = commentRequest.toEntity();
    commentRepository.insert(comment);

    plan.addComment(comment);
    planRepository.save(plan);
  }

  public List<Plan> searchByPlace(String keyword) {
    if (keyword != null) {
      return planRepository.findByPlaceName(keyword);
    } else {
      return planRepository.findAll();
    }
  }

  public void updatePlanFromUserPlanInfo(Plan plan, UserPlan userPlan) {
    plan.updateUserPlan(userPlan);
    planRepository.save(plan);
  }
}
