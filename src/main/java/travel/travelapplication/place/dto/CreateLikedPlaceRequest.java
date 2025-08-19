package travel.travelapplication.place.dto;

import travel.travelapplication.place.domain.Place;

public record CreateLikedPlaceRequest(
    Place place
) {

}
