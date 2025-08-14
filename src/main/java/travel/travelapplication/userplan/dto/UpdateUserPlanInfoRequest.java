package travel.travelapplication.userplan.dto;

import travel.travelapplication.constant.Status;

public record UpdateUserPlanInfoRequest(
    String name,
    Status status
) {

}
