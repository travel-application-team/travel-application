package travel.travelapplication.user.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.auth.dto.SessionUser;
import travel.travelapplication.constant.Status;
import travel.travelapplication.dto.userplan.LikedPlaceList;
import travel.travelapplication.dto.userplan.UpdateUserPlanInfoRequest;
import travel.travelapplication.dto.userplan.UserPlanInfoResponse;
import travel.travelapplication.place.application.PlaceService;
import travel.travelapplication.place.application.ProvCityService;
import travel.travelapplication.place.application.RecommendationService;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.place.domain.ProvCity;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.domain.UserPlan;
import travel.travelapplication.user.application.UserPlanService;

import java.util.*;
import travel.travelapplication.user.repository.UserPlanRepository;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user-plan")
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
    public String userPlanList(@AuthenticationPrincipal CustomOAuth2User oAuth2User, Model model)
            throws IllegalAccessException {
        User user = userService.findUserByEmail(oAuth2User);
        List<UserPlan> userPlans = userPlanService.findAllUserPlan(user);
        model.addAttribute("userPlans", userPlans);

        return "html/user-plan-list";
    }

    @GetMapping("/new")
    public String createUserPlan(Model model) {
        model.addAttribute("infoSubmitted", false);

        return "html/new-user-plan";
    }

    @ModelAttribute("statuses")
    public Status[] statuses() {
        return Status.values();
    }

    @ModelAttribute("cities")
    public Map<String, String> cities() {
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
        return cities;
    }

    @GetMapping("/districts")
    @ResponseBody
    public List<String> getDistricts(@RequestParam("city") String city) {
        List<String> districts = new ArrayList<>();
        Optional<ProvCity> provCities = provCityService.getCity(city);

        if (provCities.isPresent()) {
            ProvCity provCity = provCities.get();
            districts = provCity.getDistricts();
        }

        return districts;
    }

    @PostMapping("/plan-info")
    public String saveUserPlan(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                               @ModelAttribute("userPlan") UserPlanInfoResponse userPlanInfoResponse,
                               Model model)
            throws IllegalAccessException {
        User userInfo = userService.findUserByEmail(oAuth2User);
        UserPlan userPlan = userPlanService.createNewUserPlan(userInfo, userPlanInfoResponse);
        recommendationService.sendUserPlanInfo(userPlan, userInfo);

        model.addAttribute("infoSubmitted", true);

        model.addAttribute("userPlanId", userPlan.getId());
        log.info("userPlanId: {} ", userPlan.getId());
        model.addAttribute("userPlan", userPlanInfoResponse);
        model.addAttribute("user", userInfo);

        model.addAttribute("likedPlaces", userInfo.getLikedPlaces());

        return "html/new-user-plan";
    }

    @GetMapping("/{userPlanId}")
    public String userPlan(@PathVariable("userPlanId") ObjectId userPlanId, Model model) throws IllegalAccessException {
        UserPlan userPlan = userPlanService.findUserPlanById(userPlanId);
        model.addAttribute("userPlan", userPlan);

        return "html/user-plan";
    }

    @GetMapping("/{userPlanId}/places")
    public String selectLikedPlaces(HttpServletRequest request, Model model,
                                    @PathVariable("userPlanId") ObjectId userPlanId) throws IllegalAccessException {
        UserPlan userPlan = userPlanService.findUserPlanById(userPlanId);

        HttpSession session = request.getSession();
        List<SessionUser> places = (List<SessionUser>) session.getAttribute("recommendation-result");

        model.addAttribute("userPlan", userPlan);
        model.addAttribute("likedPlaceList", new LikedPlaceList());
        model.addAttribute("places", places);

        return "test/selectLikedPlacesForm";
    }

    @PostMapping("/save-places")
    @ResponseBody
    public Map<String, Object> savePlacesToUserPlan(@RequestBody List<String> selectedPlaceId,
                                                    Model model,
                                                    UserPlanInfoResponse userPlanInfo,
                                                    @AuthenticationPrincipal CustomOAuth2User oAuth2User)
            throws IllegalAccessException {
        User user = userService.findUserByEmail(oAuth2User);
        UserPlan userPlan = new UserPlan();

        List<Place> selectedPlaces = new ArrayList<>();

        for (String placeId : selectedPlaceId) {
            Place place = placeService.findByPlaceId(placeId);
            selectedPlaces.add(place);
        }
        model.addAttribute("selectedPlaces", selectedPlaces);

        userPlanService.mergePlacesToUserPlanInfo(userPlan, selectedPlaces);
        log.info("merge successful");

        model.addAttribute("userPlan", userPlan);
        log.info("userPlan added");

        // JSON으로 변환
        Map<String, Object> response = new HashMap<>();
//        response.put("selectedPlaces", selectedPlaces);
//        response.put("userPlan", userPlanInfo);
        response.put("redirectUrl", "/home");

        log.info(selectedPlaces.toString());

        return response;
    }

    @GetMapping("/{userPlanId}/user-plan-info")
    public String updateUserPlanNameAndStatusForm(@PathVariable("userPlanId") ObjectId userPlanId,
                                                  Model model) throws IllegalAccessException {
        UserPlan userPlan = userPlanService.findUserPlanById(userPlanId);
        model.addAttribute("userPlan", userPlan);

        return "test/editUserPlanInfoForm";
    }

    @PostMapping("/{userPlanId}/user-plan-info")
    public String updateUserPlanNameAndStatus(@PathVariable("userPlanId") ObjectId userPlanId,
                                              @ModelAttribute("updateUserPlan") UpdateUserPlanInfoRequest userPlanInfo,
                                              @AuthenticationPrincipal CustomOAuth2User oAuth2User)
            throws IllegalAccessException {
        User user = userService.findUserByEmail(oAuth2User);
        UserPlan userPlan = userPlanService.findUserPlanById(userPlanId);

        userPlanService.updateUserPlanInfo(user, userPlan, userPlanInfo);

        return "redirect:/home";
    }
}
