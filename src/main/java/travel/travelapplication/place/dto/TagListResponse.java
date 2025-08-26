package travel.travelapplication.place.dto;

import java.util.List;
import travel.travelapplication.place.domain.Tag;

public record TagListResponse(
    List<String> tagNames
) {

  public static TagListResponse fromEntity(List<Tag> tags) {
    List<String> tagNames = tags.stream()
        .map(Tag::getName).toList();

    return new TagListResponse(tagNames);
  }
}
