package travel.travelapplication.place.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.place.repository.PlaceRepository;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final UserService userService;

  public Place findByName(String name) {
    return placeRepository.findByName(name)
        .orElseThrow(() -> new IllegalArgumentException("place not found : " + name));
  }

  public Place findById(ObjectId id) {
    return placeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("place not found : " + id));
  }

  public Place findByPlaceId(String placeId) {
    return placeRepository.findByPlaceId(placeId)
        .orElseThrow(() -> new IllegalArgumentException("place not found: " + placeId));
  }

  public boolean toggleLikePlace(User user, Long placeId) throws IllegalAccessException {
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

