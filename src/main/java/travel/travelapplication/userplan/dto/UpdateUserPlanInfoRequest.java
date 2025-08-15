package travel.travelapplication.userplan.dto;

import travel.travelapplication.userplan.constant.Status;

public record UpdateUserPlanInfoRequest(
    String name,
    Status status
) {

}
