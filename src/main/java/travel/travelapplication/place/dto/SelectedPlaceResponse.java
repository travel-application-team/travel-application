package travel.travelapplication.place.dto;

import travel.travelapplication.place.domain.Place;

import java.util.List;

public record SelectedPlaceResponse(
    List<Place> selectedPlaces
){

}
