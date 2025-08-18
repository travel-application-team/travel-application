package travel.travelapplication.plan.dto;

import java.util.List;
import travel.travelapplication.plan.domain.Comment;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.userplan.domain.UserPlan;

public record PlanResponse(
    Plan plan,
    UserPlan userPlan,
    List<Plan> savedPlans,
    List<Comment> comments
) {

}
