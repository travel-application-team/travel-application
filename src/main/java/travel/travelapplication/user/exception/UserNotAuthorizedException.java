package travel.travelapplication.user.exception;

import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class UserNotAuthorizedException extends TravelException {

  public UserNotAuthorizedException() {
    super(ErrorCode.USER_NOT_AUTHORIZED);
  }
}
