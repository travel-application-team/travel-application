package travel.travelapplication.place.exception;

import java.util.Map;
import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class CityNotFoundException extends TravelException {

  public CityNotFoundException(String name) {
    super(ErrorCode.CITY_NOT_FOUND, Map.of("name", name));
  }
}
