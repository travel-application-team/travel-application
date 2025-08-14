package travel.travelapplication.place.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import travel.travelapplication.place.dto.ApiRequest;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.place.dto.MapApiResponse;
import travel.travelapplication.place.dto.MobilityApiResponse;
import travel.travelapplication.userplan.domain.UserPlan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KakaoMobilityService {
    private final WebClient webClient;

    private final KakaoMapService mapService;

    public KakaoMobilityService(KakaoMapService mapService, WebClient.Builder webClientBuilder,
                                @Value("${spring.api.kakao-rest-api-key}") String REST_API_KEY) {
        this.mapService=mapService;

        this.webClient = webClientBuilder
                .baseUrl("https://apis-navi.kakaomobility.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + REST_API_KEY)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<List<MobilityApiResponse>> callKakaoMobilityApi(UserPlan userPlan) throws JsonProcessingException {
        List<Place> userPlanPlaces = userPlan.getPlaces();

        List<MapApiResponse> responses = getLocation(userPlanPlaces);

        return callApi(responses);
    }

    private List<MapApiResponse> getLocation(List<Place> userPlanPlaces) {
        List<String> places = userPlanPlaces.stream()
                .map(Place::getName)
                .toList();

        Flux<String> placeData = Flux.fromIterable(places);
        Mono<List<MapApiResponse>> result = mapService.getMapSearchResult(placeData);

        return result.block();
    }

    private Mono<List<MobilityApiResponse>> callApi(List<MapApiResponse> responses) throws JsonProcessingException {
        ApiRequest request = createApiRequest(responses);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/waypoints/directions")
                        .build())
                .bodyValue(json)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .flatMap(responseMap -> {
                    List<Map<String, Object>> routes=(List<Map<String, Object>>)responseMap.get("routes");
                    List<MobilityApiResponse> apiResponses=new ArrayList<>();

                    for(Map<String, Object> route:routes) {
                        List<Map<String, Object>> sections=(List<Map<String, Object>>)route.get("sections");

                        for(Map<String, Object> section:sections) {
                            List<Map<String, Object>> guides=(List<Map<String, Object>>) section.get("guides");

                            for(Map<String, Object> guide:guides) {
                                MobilityApiResponse mobility=new MobilityApiResponse(
                                        (String) guide.get("name"),
                                        (Double) guide.get("x"),
                                        (Double) guide.get("y"),
                                        (Integer) guide.get("distance"),
                                        (Integer) guide.get("duration")
                                );
                                apiResponses.add(mobility);
                            }
                        }
                    }
                    return Mono.just(apiResponses);
                });
    }

    private ApiRequest createApiRequest(List<MapApiResponse> responses) {
        ApiRequest request = new ApiRequest();
        MapApiResponse originResp = responses.get(0);
        ApiRequest.Location origin = createApiRequestLocation(originResp);
        request.setOrigin(origin);

        List<ApiRequest.Location> list = new LinkedList<>();
        for(int i = 1; i< responses.size()-1; i++) {
            MapApiResponse waypointResp = responses.get(i);
            ApiRequest.Location waypoint = createApiRequestLocation(waypointResp);
            list.add(waypoint);
        }
        request.setWaypoints(list);

        MapApiResponse destResp = responses.get(responses.size()-1);
        ApiRequest.Location destination = createApiRequestLocation(destResp);
        request.setDestination(destination);

        return request;
    }

    private ApiRequest.Location createApiRequestLocation(MapApiResponse response) {
        String name = response.getPlaceName();
        Double X = Double.parseDouble(response.getX());
        Double Y = Double.parseDouble(response.getY());

        return new ApiRequest.Location(name, X, Y);
    }
}
