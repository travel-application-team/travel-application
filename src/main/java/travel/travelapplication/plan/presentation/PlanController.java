package travel.travelapplication.plan.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.plan.dto.CommentRequest;
import travel.travelapplication.plan.dto.AllPlanResponse;
import travel.travelapplication.plan.dto.PlanResponse;
import travel.travelapplication.plan.dto.PlanSearchResponse;
import travel.travelapplication.plan.dto.ReplyRequest;
import travel.travelapplication.plan.application.CommentService;
import travel.travelapplication.plan.domain.Comment;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.plan.application.PlanService;
import travel.travelapplication.plan.repository.PlanRepository;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;

import java.util.List;
import travel.travelapplication.userplan.domain.UserPlan;

@RequiredArgsConstructor
@RestController
@RequestMapping("/plans")
@Slf4j
public class PlanController {

  private final PlanService planService;
  private final PlanRepository planRepository;
  private final UserService userService;
  private final CommentService commentService;

  @GetMapping("/community")
  public ResponseEntity<List<AllPlanResponse>> allPlans() {
    List<Plan> plans = planRepository.findAll();
    List<AllPlanResponse> allPlans = planService.mapToAllPlanResponses(plans);

    return ResponseEntity.ok(allPlans);
  }

  @GetMapping("/community/{planId}")
  public ResponseEntity<PlanResponse> plan(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
      @PathVariable("planId") ObjectId planId) throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);

    Plan plan = planService.findById(planId);
    UserPlan userPlan = plan.getUserPlan();

    PlanResponse planResponse = planService.mapToPlanResponse(userPlan, plan, user);

    return ResponseEntity.ok(planResponse);
  }

  @PostMapping("/community/save/{planId}")
  public ResponseEntity<Boolean> savePlanToUser(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User,
      @PathVariable("planId") ObjectId planId)
      throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);
    Plan plan = planService.findById(planId);

    boolean isSaved = planService.toggleSavePlan(user, plan);

    return ResponseEntity.ok(isSaved);
  }

  @PostMapping("/community/{planId}/comment")
  public ResponseEntity<CommentRequest> saveCommentToPlan(@PathVariable("planId") ObjectId planId,
      @RequestBody CommentRequest commentRequest) {
    Plan plan = planService.findById(planId);

    planService.saveCommentToPlan(plan, commentRequest);

    return ResponseEntity.ok(commentRequest);
  }

  @PostMapping("/community/{commentId}/reply")
  public ResponseEntity<ReplyRequest> saveReplyToComment(
      @PathVariable("commentId") ObjectId commentId,
      @RequestBody ReplyRequest replyRequest) {
    Comment comment = commentService.findById(commentId);
    commentService.saveReplyToComment(comment, replyRequest);

    return ResponseEntity.ok(replyRequest);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Plan> findPlan(@RequestParam(name = "id") ObjectId id) {
    Plan plan = planService.findById(id);
    return ResponseEntity.ok(plan);
  }

  @DeleteMapping("/{id}")
  public void deletePlan(@RequestParam(name = "id") ObjectId id) {
  }

  @GetMapping("/search")
  public ResponseEntity<List<PlanSearchResponse>> planView(
      @RequestParam(value = "keyword", required = false) String keyword) {
    List<Plan> plans = planService.searchByPlace(keyword);

    List<PlanSearchResponse> planSearchResponses = planService.mapToPlanSearchResponses(plans);

    return ResponseEntity.ok(planSearchResponses);
  }
}
