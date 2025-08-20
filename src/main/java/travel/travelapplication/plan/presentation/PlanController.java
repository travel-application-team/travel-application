package travel.travelapplication.plan.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.plan.dto.CommentRequest;
import travel.travelapplication.plan.dto.PlanListItemResponse;
import travel.travelapplication.plan.dto.PlanResponse;
import travel.travelapplication.plan.dto.PlanSearchResponse;
import travel.travelapplication.plan.dto.ReplyRequest;
import travel.travelapplication.plan.application.CommentService;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.plan.application.PlanService;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/community")
@Slf4j
public class PlanController {

  private final PlanService planService;
  private final UserService userService;
  private final CommentService commentService;

  @GetMapping
  public ResponseEntity<List<PlanListItemResponse>> allPlans() {
    List<PlanListItemResponse> allPlans = planService.findAll();

    return ResponseEntity.ok(allPlans);
  }

  @GetMapping("/{planId}")
  public ResponseEntity<PlanResponse> plan(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
      @PathVariable("planId") ObjectId planId) throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);

    PlanResponse planResponse = planService.findPlan(planId, user);

    return ResponseEntity.ok(planResponse);
  }

  @PostMapping("/{planId}/save")
  public ResponseEntity<Boolean> savePlanToUser(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User,
      @PathVariable("planId") ObjectId planId)
      throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);

    boolean isSaved = planService.toggleSavePlan(user, planId);

    return ResponseEntity.ok(isSaved);
  }

  @PostMapping("/{planId}/comment")
  public ResponseEntity<CommentRequest> saveCommentToPlan(@PathVariable("planId") ObjectId planId,
      @RequestBody CommentRequest commentRequest) {
    Plan plan = planService.findById(planId);

    planService.saveCommentToPlan(plan, commentRequest);

    return ResponseEntity.ok(commentRequest);
  }

  @PostMapping("/{commentId}/reply")
  public ResponseEntity<ReplyRequest> saveReplyToComment(
      @PathVariable("commentId") ObjectId commentId,
      @RequestBody ReplyRequest replyRequest) {
    commentService.saveReplyToComment(commentId, replyRequest);

    return ResponseEntity.ok(replyRequest);
  }

  @GetMapping("/search")
  public ResponseEntity<List<PlanSearchResponse>> planView(
      @RequestParam(value = "keyword", required = false) String keyword) {
    List<PlanSearchResponse> planSearchResponses = planService.searchByPlace(keyword);

    return ResponseEntity.ok(planSearchResponses);
  }
}
