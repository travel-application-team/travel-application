package travel.travelapplication.place.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.place.exception.PlaceNotFoundException;
import travel.travelapplication.place.repository.PlaceRepository;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;

@Service
@RequiredArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final UserService userService;

  public Place findByName(String name) {
    return placeRepository.findByName(name)
        .orElseThrow(() -> new PlaceNotFoundException(name));
  }

  public Place findById(ObjectId id) {
    return placeRepository.findById(id)
        .orElseThrow(() -> new PlaceNotFoundException(id.toString()));
  }

  public Place findByPlaceId(String placeId) {
    return placeRepository.findByPlaceId(placeId)
        .orElseThrow(() -> new PlaceNotFoundException(placeId));
  }

  public boolean toggleLikePlace(User user, Long placeId) {
    List<Long> likedPlaces = user.getLikedPlaces();
    boolean isLiked = likedPlaces.stream().anyMatch(likedId -> likedId.equals(placeId));

    if (!isLiked) {
      likedPlaces.add(placeId);
    } else {
      likedPlaces.removeIf(likedId -> likedId.equals(placeId));
    }
    userService.updateLikedPlaces(user, likedPlaces);
    return !isLiked;
  }
}

