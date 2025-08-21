package travel.travelapplication.user.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.place.domain.Tag;
import travel.travelapplication.place.repository.TagRepository;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.dto.UserResponse;
import travel.travelapplication.user.exception.UserNotAuthorizedException;
import travel.travelapplication.user.exception.UserNotFoundException;
import travel.travelapplication.user.repository.UserRepository;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.userplan.dto.UserPlanListItemResponse;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final TagRepository tagRepository;

  public void save(User user) {
    userRepository.save(user);
  }

  public void updateUserName(User user, String username) {
    if (user != null) {
      user.updateName(username);
    } else {
      throw new UserNotFoundException();
    }
  }

  public void addTag(User user, List<String> tagIds) {
    List<ObjectId> objectIdList = tagIds.stream()
        .map(ObjectId::new)
        .collect(Collectors.toList());
    List<Tag> tags = tagRepository.findAllById(objectIdList);

    if (user != null) {
      user.updateTags(tags);
    } else {
      throw new UserNotFoundException();
    }
  }

  public void updateSavedPlans(User user, List<Plan> savedPlans) {
    if (user != null) {
      user.updateSavedPlans(savedPlans);
    } else {
      throw new UserNotFoundException();
    }
  }

  public List<Tag> findAllTag(User user) {
    return user.getTags();
  }

  public User findUserByEmail(@AuthenticationPrincipal CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    if (oAuth2User == null) {
      throw new UserNotAuthorizedException();
    }

    String provider = oAuth2User.getRegistrationId();
    String email;

    if (provider.equals("google")) {
      email = oAuth2User.getEmail();
    } else {
      Map<Object, String> response = (Map<Object, String>) oAuth2User.getAttribute("response");
      email = response.get("email");
    }

    User user = userRepository.findByEmail(email)
        .orElse(null);
    if (user != null) {
      return user;
    } else {
      throw new UserNotFoundException();
    }
  }

  public void updateLikedPlaces(User user, List<Long> likedPlaces) {
    if (user != null) {
      user.updateLikedPlaces(likedPlaces);
    } else {
      throw new UserNotFoundException();
    }
  }

  public UserResponse getUserInfo(CustomOAuth2User oAuth2User) throws IllegalAccessException {
    User user = findUserByEmail(oAuth2User);
    return UserResponse.fromEntity(user);
  }

  public List<UserPlanListItemResponse> findUserPlanList(CustomOAuth2User oAuth2User)
      throws IllegalAccessException {
    User user = findUserByEmail(oAuth2User);
    List<UserPlan> userPlans = user.getUserPlans();

    return userPlans.stream()
        .map(UserPlanListItemResponse::fromEntity).toList();
  }
}
