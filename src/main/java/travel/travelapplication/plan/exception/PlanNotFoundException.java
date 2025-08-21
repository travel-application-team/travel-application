package travel.travelapplication.plan.exception;

import java.util.Map;
import org.bson.types.ObjectId;
import travel.travelapplication.exception.ErrorCode;
import travel.travelapplication.exception.TravelException;

public class PlanNotFoundException extends TravelException {

  public PlanNotFoundException(ObjectId id) {
    super(ErrorCode.PLAN_NOT_FOUND, Map.of("id", id));
  }
}
