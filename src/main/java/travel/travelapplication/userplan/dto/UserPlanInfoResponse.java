package travel.travelapplication.userplan.dto;

import java.time.LocalDate;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.userplan.domain.UserPlan.Status;

public record UserPlanInfoResponse(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    Long budget,
    String city,
    String district,
    Status status
) {

  public static UserPlanInfoResponse fromEntity(UserPlan userPlan) {
    return new UserPlanInfoResponse(userPlan.getName(), userPlan.getStartDate(),
        userPlan.getEndDate(),
        userPlan.getBudget(), userPlan.getCity(), userPlan.getDistrict(), userPlan.getStatus());
  }
}
