package travel.travelapplication.user.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.user.repository.UserRepository;
import travel.travelapplication.util.CsvWriter;

@Service
@RequiredArgsConstructor
public class UserLikeCsvExportService {

  private final UserRepository userRepository;
  private final CsvWriter csvWriter;

  private static String safe(String s) {
    if (s == null || s.isEmpty()) {
      return "";
    }
    char c = s.charAt(0);
    if (c == '=' || c == '+' || c == '-' || c == '@') {
      return "'" + s;
    }
    return s;
  }

  @Transactional(readOnly = true)
  public String exportAllUserLikes() {
    List<User> users = userRepository.findAll();

    List<String[]> rows = new ArrayList<>();
    for (User u : users) {
      if (u.getLikedPlaces() == null || u.getLikedPlaces().isEmpty()) {
        continue;
      }
      for (Long placeId : u.getLikedPlaces()) {
        rows.add(new String[]{safe(u.getEmail()), placeId.toString()});
      }
    }

    return csvWriter.writeWithHeader(
        new String[]{"email", "placeId"},
        rows
    );
  }
}
