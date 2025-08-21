package travel.travelapplication.user.exception;

import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class UserNotFoundException extends TravelException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }
}
