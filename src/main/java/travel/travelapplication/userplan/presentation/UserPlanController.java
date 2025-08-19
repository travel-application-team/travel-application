package travel.travelapplication.userplan.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.place.application.ProvCityService;
import travel.travelapplication.place.constant.City;
import travel.travelapplication.recommendation.application.RecommendationService;
import travel.travelapplication.recommendation.domain.Recommendation;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.application.UserPlanService;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.userplan.dto.UpdateUserPlanInfoRequest;
import travel.travelapplication.userplan.dto.UserPlanInfoRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user-plans")
public class UserPlanController {

  private final UserPlanService userPlanService;
  private final UserService userService;
  private final RecommendationService recommendationService;
  private final ProvCityService provCityService;

  @GetMapping("/{id}")
  public ResponseEntity<UserPlan> userPlan(@PathVariable("id") ObjectId id)
      throws IllegalAccessException {
    UserPlan userPlan = userPlanService.findById(id);
    return ResponseEntity.ok(userPlan);
  }

  @GetMapping("/districts")
  @ResponseBody
  public ResponseEntity<List<String>> getDistricts(@RequestParam("city") City city) {
    List<String> districts = provCityService.getDistrictsByCity(city);
    return ResponseEntity.ok(districts);
  }

  @PostMapping("/plan-info")
  public ResponseEntity<List<Recommendation>> getRecommendations(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User,
      @RequestBody UserPlanInfoRequest userPlanInfoRequest)
      throws IllegalAccessException {
    User userInfo = userService.findUserByEmail(oAuth2User);
    UserPlan userPlan = userPlanService.create(userInfo, userPlanInfoRequest);
    List<Recommendation> recommendations = recommendationService.getRecommendationsByUserPlanInfo(
        userPlan,
        userInfo);

    return ResponseEntity.ok(recommendations);
  }

  @GetMapping("/{userPlanId}")
  public ResponseEntity<UserPlan> getUserPlan(@PathVariable("userPlanId") ObjectId userPlanId)
      throws IllegalAccessException {
    UserPlan userPlan = userPlanService.findById(userPlanId);

    return ResponseEntity.ok(userPlan);
  }

  @PostMapping("/save-places")
  @ResponseBody
  public ResponseEntity<UserPlan> savePlacesToUserPlan(@RequestBody List<String> selectedPlaceId) {
    UserPlan userPlan = userPlanService.mergePlacesToUserPlanInfo(selectedPlaceId);

    return ResponseEntity.ok(userPlan);
  }

  @PutMapping("/{userPlanId}/user-plan-info")
  public ResponseEntity<UserPlan> updateUserPlanNameAndStatus(
      @PathVariable("userPlanId") ObjectId userPlanId,
      @RequestBody UpdateUserPlanInfoRequest userPlanInfo,
      @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    UserPlan userPlan = userPlanService.updateUserPlanInfo(oAuth2User, userPlanId, userPlanInfo);

    return ResponseEntity.ok(userPlan);
  }

  @PutMapping("/{userPlanId}/places")
  public ResponseEntity<UserPlan> updateUserPlanPlaces(
      @PathVariable("userPlanId") ObjectId userPlanId,
      @RequestBody List<String> placeIds) throws IllegalAccessException {
    UserPlan userPlan = userPlanService.updateUserPlanPlaces(userPlanId, placeIds);
    return ResponseEntity.ok(userPlan);
  }

}
