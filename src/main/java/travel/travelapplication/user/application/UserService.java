package travel.travelapplication.user.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.auth.dto.JoinRequest;
import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.place.domain.Tag;
import travel.travelapplication.place.dto.TagListResponse;
import travel.travelapplication.place.exception.InvalidTagSelectionException;
import travel.travelapplication.place.repository.TagRepository;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.dto.UserResponse;
import travel.travelapplication.user.exception.DuplicateResourceException;
import travel.travelapplication.user.exception.PasswordValidationException;
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
  private final BCryptPasswordEncoder encoder;

  public void save(User user) {
    userRepository.save(user);
  }

  public UserResponse join(JoinRequest joinRequest) {
    if (checkNameDuplicate(joinRequest.name())) {
      throw new DuplicateResourceException(ErrorCode.NAME_ALREADY_EXISTS);
    }

    if (checkEmailDuplicate(joinRequest.email())) {
      throw new DuplicateResourceException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    if (checkPasswordMatch(joinRequest.password(), joinRequest.checkPassword())) {
      throw new PasswordValidationException();
    }

    checkUserDuplicate(joinRequest.toEntity(encoder.encode(joinRequest.password())));
    User user = userRepository.save(joinRequest.toEntity(encoder.encode(joinRequest.password())));

    return UserResponse.fromEntity(user);
  }

  private void checkUserDuplicate(User user) {
    userRepository.findByEmail(user.getEmail())
        .ifPresent(m -> {
          throw new DuplicateResourceException(ErrorCode.USER_ALREADY_EXISTS);
        });
  }

  private boolean checkNameDuplicate(String username) {
    return userRepository.existsByName(username);
  }

  private boolean checkEmailDuplicate(String email) {
    return userRepository.findByEmail(email).isEmpty();
  }

  private boolean checkPasswordMatch(String password, String checkPassword) {
    return password.equals(checkPassword);
  }

  public void updateUserName(User user, String username) {
    if (user != null) {
      user.updateName(username);
    } else {
      throw new UserNotFoundException();
    }
  }

  public TagListResponse addTag(CustomOAuth2User oAuth2User, List<String> tagIds) {
    if (tagIds.size() < 3) {
      throw new InvalidTagSelectionException(tagIds.size());
    }

    List<ObjectId> objectIdList = tagIds.stream()
        .map(ObjectId::new)
        .collect(Collectors.toList());
    List<Tag> tags = tagRepository.findAllById(objectIdList);

    User user = findUserByEmail(oAuth2User);

    if (user != null) {
      user.updateTags(tags);
    } else {
      throw new UserNotFoundException();
    }

    return TagListResponse.fromEntity(tags);
  }

  public void updateSavedPlans(User user, List<Plan> savedPlans) {
    if (user != null) {
      user.updateSavedPlans(savedPlans);
    } else {
      throw new UserNotFoundException();
    }
  }

  public TagListResponse findAllTag(CustomOAuth2User oAuth2User) {
    User user = findUserByEmail(oAuth2User);

    return TagListResponse.fromEntity(user.getTags());
  }

  public User findUserByEmail(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
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

  public UserResponse getUserInfo(CustomOAuth2User oAuth2User) {
    User user = findUserByEmail(oAuth2User);
    return UserResponse.fromEntity(user);
  }

  public List<UserPlanListItemResponse> findUserPlanList(CustomOAuth2User oAuth2User) {
    User user = findUserByEmail(oAuth2User);
    List<UserPlan> userPlans = user.getUserPlans();

    return userPlans.stream()
        .map(UserPlanListItemResponse::fromEntity).toList();
  }
}
