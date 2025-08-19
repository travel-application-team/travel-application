package travel.travelapplication.userplan.dto;

import java.time.LocalDate;
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

}
