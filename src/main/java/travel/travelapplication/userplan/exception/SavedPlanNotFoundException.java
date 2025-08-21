package travel.travelapplication.userplan.exception;

import java.util.Map;
import org.bson.types.ObjectId;
import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class SavedPlanNotFoundException extends TravelException {

  public SavedPlanNotFoundException(ObjectId id) {
    super(ErrorCode.SAVED_PLAN_NOT_FOUND, Map.of("id", id));
  }
}
