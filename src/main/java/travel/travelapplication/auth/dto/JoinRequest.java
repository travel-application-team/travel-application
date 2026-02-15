package travel.travelapplication.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import travel.travelapplication.auth.constant.Role;
import travel.travelapplication.user.domain.User;

public record JoinRequest(
    @NotBlank(message = "이름은 필수입니다.")
    String name,

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    String password,

    @NotBlank(message = "이메일 확인은 필수입니다.")
    String checkPassword
) {

  public User toEntity(String encodedPassword) {
    return User.builder()
        .name(this.name)
        .email(this.email)
        .password(encodedPassword)
        .role(Role.USER.getKey())
        .build();
  }
}
