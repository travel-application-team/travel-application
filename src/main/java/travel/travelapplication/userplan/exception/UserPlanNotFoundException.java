package travel.travelapplication.userplan.exception;

import java.util.Map;
import org.bson.types.ObjectId;
import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class UserPlanNotFoundException extends TravelException {

  public UserPlanNotFoundException(ObjectId id) {
    super(ErrorCode.USER_PLAN_NOT_FOUND, Map.of("id", id));
  }
}
