package travel.travelapplication.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.auth.dto.OAuthAttributes;
import travel.travelapplication.constant.Role;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    log.info("registrationId: {}", registrationId);

    String userNameAttributeName = userRequest
        .getClientRegistration().getProviderDetails()
        .getUserInfoEndpoint().getUserNameAttributeName();

    String role = Role.USER.getKey();
    String accessToken = userRequest.getAccessToken().getTokenValue();

    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
        oAuth2User.getAttributes());

    User findUser = saveUser(attributes, role, accessToken);

    String principalName = findUser.getEmail();
    if (principalName == null || principalName.isEmpty()) {
      throw new IllegalArgumentException("principalName cannot be empty");
    }

    return new CustomOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(findUser.getRole())),
        oAuth2User.getAttributes(),
        attributes.getNameAttributeKey(),
        findUser.getEmail(),
        findUser.getName(),
        findUser.getRole(),
        registrationId
    );
  }

  private User saveUser(OAuthAttributes attributes, String role, String accessToken) {

    User findUser = getUser(attributes);
    String name = attributes.getName();
    if (name == null) {
      name = "anonymous";
    }

    if (findUser == null) {

      User user = User.builder()
          .name(name)
          .email(attributes.getEmail())
          .role(role)
          .accessToken(accessToken)
          .userPlans(null)
          .likedPlaces(null)
          .tags(null)
          .savedPlans(null)
          .build();
      userRepository.insert(user);
      return user;
    } else {
      // 기존 유저 이름이 null인 경우 anonymous로 이름 변경
      findUser.setName(name);
      findUser.setAccessToken(accessToken);
      userRepository.save(findUser);
      return findUser;
    }
  }

  private User getUser(OAuthAttributes attributes) {
    return userRepository.findByEmail(attributes.getEmail())
        .orElse(null);
  }
}
