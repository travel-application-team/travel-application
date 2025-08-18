package travel.travelapplication.userplan.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.auth.dto.SessionUser;
import travel.travelapplication.place.application.PlaceService;
import travel.travelapplication.place.application.ProvCityService;
import travel.travelapplication.place.dto.CreateLikedPlaceRequest;
import travel.travelapplication.recommendation.application.RecommendationService;
import travel.travelapplication.recommendation.domain.Recommendation;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.application.UserPlanService;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.userplan.dto.UpdateUserPlanInfoRequest;
import travel.travelapplication.userplan.dto.UserPlanInfoRequest;
import travel.travelapplication.userplan.repository.UserPlanRepository;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user-plans")
public class UserPlanController {

  private final UserPlanService userPlanService;
  private final UserPlanRepository userPlanRepository;
  private final UserService userService;
  private final PlaceService placeService;
  private final RecommendationService recommendationService;
  private final ProvCityService provCityService;

  @GetMapping("/single")
  public String userPlan() {
    return "html/user-plan";
  }

  @GetMapping("/list")
  public ResponseEntity<List<UserPlan>> userPlanList(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User, Model model)
      throws IllegalAccessException {
    User user = userService.findUserByEmail(oAuth2User);
    List<UserPlan> userPlans = userPlanService.findAll(user);

    return ResponseEntity.ok(userPlans);
  }

  // 서비스로 옮길지, enum으로 바꿀지 고민
  @GetMapping("/cities")
  public ResponseEntity<Map<String, String>> cities() {
    Map<String, String> cities = new LinkedHashMap<>();
    cities.put("SEOUL", "서울");
    cities.put("GYEONGGI", "경기");
    cities.put("INCHEON", "인천");
    cities.put("GANGWON", "강원");
    cities.put("DAEJEON", "대전");
    cities.put("SEJONG", "세종");
    cities.put("CHUNGNAM", "충남");
    cities.put("CHUNGBUK", "충북");
    cities.put("BUSAN", "부산");
    cities.put("ULSAN", "울산");
    cities.put("GYEONGNAM", "경남");
    cities.put("GYEONGBUK", "경북");
    cities.put("DAEGU", "대구");
    cities.put("GWANGJU", "광주");
    cities.put("JEONNAM", "전남");
    cities.put("JEONBUK", "전북");
    cities.put("JEJU", "제주");
    return ResponseEntity.ok(cities);
  }

  @GetMapping("/districts")
  @ResponseBody
  public ResponseEntity<List<String>> getDistricts(@RequestParam("city") String city) {
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

  // TODO: 이 api가 뭘 하는건지 잘 모르곘음
  @GetMapping("/{userPlanId}/places")
  public String selectLikedPlaces(HttpServletRequest request,
      @PathVariable("userPlanId") ObjectId userPlanId,
      @RequestBody CreateLikedPlaceRequest createLikedPlaceRequest) throws IllegalAccessException {
    UserPlan userPlan = userPlanService.findById(userPlanId);

    HttpSession session = request.getSession();
    List<SessionUser> places = (List<SessionUser>) session.getAttribute("recommendation-result");

    return "test/selectLikedPlacesForm";
  }

  @PostMapping("/save-places")
  @ResponseBody
  public ResponseEntity<UserPlan> savePlacesToUserPlan(@RequestBody List<String> selectedPlaceId,
      @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    UserPlan userPlan = userPlanService.mergePlacesToUserPlanInfo(selectedPlaceId);

    return ResponseEntity.ok(userPlan);
  }

  @PostMapping("/{userPlanId}/user-plan-info")
  public ResponseEntity<UserPlan> updateUserPlanNameAndStatus(
      @PathVariable("userPlanId") ObjectId userPlanId,
      @RequestBody UpdateUserPlanInfoRequest userPlanInfo,
      @AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    UserPlan userPlan = userPlanService.updateUserPlanInfo(oAuth2User, userPlanId, userPlanInfo);

    return ResponseEntity.ok(userPlan);
  }
}
