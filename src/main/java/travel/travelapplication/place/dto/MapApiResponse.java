package travel.travelapplication.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MapApiResponse (
    String id,
    @JsonProperty("place_name") String placeName,
    String phone,
    String link,
    String address,
    String x,
    String y
){

}
