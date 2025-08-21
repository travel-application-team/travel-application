package travel.travelapplication.place.exception;

import java.util.Map;
import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class PlaceNotFoundException extends TravelException {

  public PlaceNotFoundException(String idOrName) {
    super(ErrorCode.PLACE_NOT_FOUND, Map.of("idOrName", idOrName));
  }
}
