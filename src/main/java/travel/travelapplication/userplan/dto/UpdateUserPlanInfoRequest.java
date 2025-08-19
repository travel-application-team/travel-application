package travel.travelapplication.userplan.dto;

import travel.travelapplication.userplan.domain.UserPlan.Status;

public record UpdateUserPlanInfoRequest(
    String name,
    Status status
) {

}
