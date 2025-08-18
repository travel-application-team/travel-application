package travel.travelapplication.plan.dto;

import java.util.List;
import travel.travelapplication.plan.domain.Plan;

public record PlanSearchResponse(
    List<Plan> plans
) {

}
