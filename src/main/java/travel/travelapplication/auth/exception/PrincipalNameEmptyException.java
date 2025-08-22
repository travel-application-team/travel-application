package travel.travelapplication.auth.exception;

import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class PrincipalNameEmptyException extends TravelException {

  public PrincipalNameEmptyException() {
    super(ErrorCode.PRINCIPAL_NAME_EMPTY);
  }
}
