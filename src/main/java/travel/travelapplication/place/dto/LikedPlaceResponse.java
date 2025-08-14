package travel.travelapplication.place.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public record LikedPlaceResponse(
    List<String> likedPlaces
) {

}
