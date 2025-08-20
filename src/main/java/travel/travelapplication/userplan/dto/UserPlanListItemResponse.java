package travel.travelapplication.userplan.dto;

import java.util.Date;
import org.bson.types.ObjectId;
import travel.travelapplication.userplan.domain.UserPlan;

public record UserPlanListItemResponse(
    ObjectId id,
    String name,
    Date createdAt
) {

  public static UserPlanListItemResponse fromEntity(UserPlan userPlan) {
    return new UserPlanListItemResponse(userPlan.getId(), userPlan.getName(),
        userPlan.getCreatedAt());
  }
}
