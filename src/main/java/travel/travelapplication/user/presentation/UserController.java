package travel.travelapplication.user.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.auth.dto.JoinRequest;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.dto.UserResponse;
import travel.travelapplication.userplan.dto.UserPlanListItemResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/my-page")
public class UserController {

  private final UserService userService;

  @PostMapping("/join")
  public ResponseEntity<UserResponse> join(@Valid @RequestBody JoinRequest joinRequest) {
    UserResponse userResponse = userService.join(joinRequest);

    return ResponseEntity.ok(userResponse);
  }

  @GetMapping
  public String profile() {
    return "html/profile";
  }

  @GetMapping("/profile/username")
  public ResponseEntity<UserResponse> updateUsernameForm(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    UserResponse userResponse = userService.getUserInfo(oAuth2User);

    return ResponseEntity.ok(userResponse);
  }

  @PostMapping("/profile/username")
  public ResponseEntity<String> updateUserName(@RequestParam("editedName") String newName,
      @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);
    userService.updateUserName(user, newName);

    return ResponseEntity.ok(user.getName());
  }

  @GetMapping("/user-plan")
  public ResponseEntity<List<UserPlanListItemResponse>> userPlans(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    List<UserPlanListItemResponse> userPlanListItemResponse = userService.findUserPlanList(
        oAuth2User);

    return ResponseEntity.ok(userPlanListItemResponse);
  }
}
