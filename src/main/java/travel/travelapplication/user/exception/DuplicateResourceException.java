package travel.travelapplication.user.exception;

import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class DuplicateResourceException extends TravelException {

  public DuplicateResourceException(ErrorCode errorCode) {
    super(errorCode);
  }
}
