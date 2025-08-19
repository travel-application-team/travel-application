package travel.travelapplication.recommendation.application;

import jakarta.servlet.http.HttpSession;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import travel.travelapplication.auth.dto.SessionUser;
import travel.travelapplication.recommendation.domain.Recommendation;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.userplan.dto.SendUserPlanInfoRequest;

@Service
@Getter
public class RecommendationService {

  private final RestTemplate restTemplate;

  private final WebClient webClient;
  private final HttpSession httpSession;


  public RecommendationService(RestTemplate restTemplate, Builder builder,
      HttpSession httpSession) {
    this.restTemplate = restTemplate;
    this.webClient = builder
        .baseUrl("http://127.0.0.1:5000")
        .build();
    this.httpSession = httpSession;
  }

  @Async
  public CompletableFuture<List<Recommendation>> fetchData(String endPoint) {
    ResponseEntity<List<Recommendation>> response = restTemplate.exchange(
        "http://127.0.0.1:5000" + endPoint,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Recommendation>>() {
        }
    );
    List<Recommendation> recommendations = response.getBody();
    return CompletableFuture.completedFuture(recommendations);
  }


  public List<Recommendation> getRecommendationsByUserPlanInfo(UserPlan userPlan, User user) {

    long period = ChronoUnit.DAYS.between(userPlan.getStartDate(), userPlan.getEndDate());

    SendUserPlanInfoRequest request = new SendUserPlanInfoRequest(user.getEmail(),
        userPlan.getCity(),
        userPlan.getDistrict(), period);

    try {
      return webClient.post()
          .uri("/send-places")
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .bodyValue(request)
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<List<Recommendation>>() {
          })
          .block();
    } catch (WebClientResponseException e) {
      // 에러 처리 (예: 로그 출력)
      System.out.println(
          "Error occurred: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
      return List.of(); // 빈 리스트 반환
    } catch (Exception e) {
      System.out.println("General error: " + e.getMessage());
      return List.of();
    }
  }

//    public List<Recommendation> getRandomPlaces(UserPlan userPlan, User user) { // 다른 여행지
//        long period = ChronoUnit.DAYS.between(userPlan.getStartDate(), userPlan.getEndDate());
//
//        UserPlanInfoRequest request = new UserPlanInfoRequest(user.getEmail(), userPlan.getCity(),
//                userPlan.getDistrict(), period);
//
//        return webClient.post()
//                .uri("/random-places")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .bodyValue(request) // UserPlanInfoRequest 객체를 JSON으로 변환하여 전송
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<List<Recommendation>>() {
//                }) // 반환 타입 지정
//                .block();
//
//    }


  public void savePlacesToSession(List<Recommendation> recommendations) {
    List<SessionUser> sessions = (List<SessionUser>) httpSession.getAttribute(
        "recommendation-result");
    if (sessions == null) {
      sessions = new ArrayList<>();
    }

    for (Recommendation recommendation : recommendations) {
      SessionUser sessionUser = recommendation.toSessionUser();
      sessions.add(sessionUser);
    }
    httpSession.setAttribute("recommendation-result", sessions);
  }

}
