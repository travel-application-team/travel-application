package travel.travelapplication.user.exception;

import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class PasswordValidationException extends TravelException {

  public PasswordValidationException() {
    super(ErrorCode.PASSWORD_MISMATCH);
  }
}
