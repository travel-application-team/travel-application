package travel.travelapplication.place.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import travel.travelapplication.auth.dto.SessionUser;
import travel.travelapplication.place.dto.MapApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KakaoMapService {
    private final WebClient webClient;

    public KakaoMapService(WebClient.Builder webClientBuilder,
                           @Value("${spring.api.kakao-rest-api-key}") String REST_API_KEY) {
        this.webClient = webClientBuilder
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK "+REST_API_KEY)
                .build();
    }

    public Mono<List<MapApiResponse>> callKakaoMapApi(List<SessionUser> sessions) {
        List<String> places = new ArrayList<>();

        for(SessionUser session : sessions) {
            String place = session.getName();
            places.add(place);
        }
        Flux<String> placeData = Flux.fromIterable(places);
        Mono<List<MapApiResponse>> searchResult = getMapSearchResult(placeData);
        return searchResult;
    }

    public Mono<List<MapApiResponse>> getMapSearchResult(Flux<String> result) {
        return result.flatMap(query -> callApi(webClient, query)
                .flatMapMany(Flux::fromIterable))
                .collectList();
    }

    private Mono<List<MapApiResponse>> callApi(WebClient webClient, String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .flatMap(
                        responseMap -> {
                            List<Map<String, String>> documents = (List<Map<String, String>>) responseMap.get("documents");
                            List<MapApiResponse> list = new ArrayList<>(documents.size());

                            for (Map<String, String> document : documents) {
                                String id = document.get("id");
                                String placeName = document.get("place_name");
                                String phone = document.get("phone");
                                String link = "https://map.kakao.com/link/to/" + id;
                                String address = document.get("road_address_name");
                                String x = document.get("x");
                                String y = document.get("y");

                                MapApiResponse response = new MapApiResponse(id, placeName, phone, link, address, x, y);

                                list.add(response);
                            }
                            return Mono.just(list);
                        }
                );
    }
}
