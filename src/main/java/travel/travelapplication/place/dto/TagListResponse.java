package travel.travelapplication.place.dto;

import java.util.List;

public record TagListResponse(
    List<String> tagList
) {
  public List<String> getTagList() { return tagList; }

}
