package travel.travelapplication.place.dto;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagListForm {
  private List<String> tagList = new LinkedList<>();

}
