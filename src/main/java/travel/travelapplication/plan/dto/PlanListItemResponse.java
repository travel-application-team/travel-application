package travel.travelapplication.plan.dto;

import java.util.Date;
import org.bson.types.ObjectId;
import travel.travelapplication.plan.domain.Plan;

public record PlanListItemResponse(
    ObjectId planId,
    String name,
    Date createdAt
) {

  public static PlanListItemResponse fromEntity(Plan plan) {
    return new PlanListItemResponse(plan.getId(), plan.getName(), plan.getCreatedAt());
  }
}
