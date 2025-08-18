package travel.travelapplication.user.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.domain.UserPlan;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/my-page")
public class UserController {

  private final UserService userService;

  @GetMapping
  public String profile() {
    return "html/profile";
  }

  @GetMapping("/profile/username")
  public String updateUsernameForm(Model model,
      @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);

    model.addAttribute("oldName", user.getName());
    model.addAttribute("userEmail", user.getEmail());

    return "test/editUsername";
  }

  @PostMapping("/profile/username")
  public String updateUserName(@RequestParam("editedName") String newName,
      @RequestParam("email") String email)
      throws IllegalAccessException {

    log.info("user email: {}", email);

    userService.updateUserName(email, newName);

    log.info("new username: {}", newName);

    return "redirect:/home";
  }

  @GetMapping("/user-plan")
  public String userPlans(Model model, @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);
    List<UserPlan> userPlans = user.getUserPlans(); // UserPlanService에서 가져오기 까다로워서 메서드 삭제해서 빌드 에러 방지를 위해 임시로 수정했습니다. 충돌 시 논의하면 될 것 같습니다.

    model.addAttribute("userPlans", userPlans);

    return "test/userPlans";
  }


}
