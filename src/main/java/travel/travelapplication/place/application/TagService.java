package travel.travelapplication.place.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travelapplication.place.domain.Tag;
import travel.travelapplication.place.repository.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  public List<String> findAll() {
    List<Tag> tags = tagRepository.findAll();
    return tags.stream()
        .map(Tag::getName).toList();
  }
}
