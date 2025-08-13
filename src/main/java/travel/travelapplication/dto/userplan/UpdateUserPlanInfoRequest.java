package travel.travelapplication.dto.userplan;

import travel.travelapplication.constant.Status;

public record UpdateUserPlanInfoRequest(
    String name,
    Status status
) {

}
