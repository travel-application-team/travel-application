package travel.travelapplication.plan.dto;

import java.util.Date;
import travel.travelapplication.plan.domain.Plan;

public record PlanSearchResponse(
    String name,
    String userEmail,
    Date createdAt
) {

  public static PlanSearchResponse fromEntity(Plan plan) {
    return new PlanSearchResponse(plan.getName(), plan.getUserEmail(), plan.getCreatedAt());
  }
}
