package travel.travelapplication.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import travel.travelapplication.auth.dto.PrincipalDetails;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.exception.UserNotFoundException;
import travel.travelapplication.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
    User user = userRepository.findByEmail(username)
        .orElseThrow(UserNotFoundException::new);

    return new PrincipalDetails(user);
  }
}
