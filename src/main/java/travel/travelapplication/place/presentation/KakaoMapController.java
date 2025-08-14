package travel.travelapplication.place.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import travel.travelapplication.auth.dto.SessionUser;
import travel.travelapplication.place.application.KakaoMapService;
import travel.travelapplication.place.application.KakaoMobilityService;
import travel.travelapplication.place.response.MapApiResponse;
import travel.travelapplication.place.response.LocationDto;
import travel.travelapplication.place.response.MobilityApiResponse;
import travel.travelapplication.userplan.application.UserPlanService;
import travel.travelapplication.userplan.domain.UserPlan;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/places")
@RequiredArgsConstructor
@Slf4j
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;
    private final KakaoMobilityService kakaoMobilityService;
    private final UserPlanService userPlanService;

    private List<MapApiResponse> list;

    @GetMapping("/recommend-details")
    public String getRecommendationDetails(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        List<SessionUser> sessions = (List<SessionUser>) session.getAttribute("recommendation-result");
        Mono<List<MapApiResponse>> listMono = kakaoMapService.callKakaoMapApi(sessions);

        list = listMono.block();

        model.addAttribute("list", list);

        return "test/map-data";
    }

    @GetMapping("/map-marker")
    public String showMapWithMarker(Model model) {
        List<LocationDto> locList=new ArrayList<>();

        for(MapApiResponse response:list) {
            locList.add(new LocationDto(response.getPlaceName(), response.getY(), response.getX())); // X, Y 방향 유의
        }
        model.addAttribute("locList", locList);

        return "test/map-marker";
    }

    @GetMapping("/{userPlanId}/routes")
    public String getRoutes(@PathVariable("userPlanId") ObjectId userPlanId, Model model) throws IllegalAccessException, JsonProcessingException {
        UserPlan userPlan = userPlanService.findUserPlanById(userPlanId);
        List<MobilityApiResponse> apiResult = kakaoMobilityService.callKakaoMobilityApi(userPlan).block();

        int distance=0, duration=0;
        for(MobilityApiResponse response:apiResult) {
            distance+= response.getDistance();
            duration+= response.getDuration();
        }

        String distanceStr;
        if (distance >= 1000) {
            distanceStr = (distance / 1000.0) + " km";
        } else {
            distanceStr = distance + " m";
        }

        String durationStr;
        if (duration >= 3600) {
            int hours = duration / 3600;
            int minutes = (duration % 3600) / 60;
            durationStr = hours + " hrs " + minutes + " mins";
        } else if (duration >= 60) {
            int minutes = duration / 60;
            int seconds = duration % 60;
            durationStr = minutes + " mins " + seconds + " secs";
        } else {
            durationStr = duration + " secs";
        }

        model.addAttribute("distance", distanceStr);
        model.addAttribute("duration", durationStr);
        model.addAttribute("apiResult", apiResult);

        return "test/map-route";
    }
}
