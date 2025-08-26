package travel.travelapplication.place.exception;

import java.util.Map;
import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class InvalidTagSelectionException extends TravelException {

  public InvalidTagSelectionException(Integer size) {
    super(ErrorCode.INVALID_TAG_SELECTION, Map.of("providedSize", size, "requiredSize", 3));
  }
}
