package travel.travelapplication.user.dto;

import travel.travelapplication.user.domain.User;

public record UserResponse(
    String username,
    String email
) {

  public static UserResponse fromEntity(User user) {
    return new UserResponse(user.getName(), user.getEmail());
  }
}
