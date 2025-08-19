package travel.travelapplication.plan.dto;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import travel.travelapplication.userplan.dto.UserPlanInfoResponse;

public record PlanResponse(
    String userEmail,
    Date createdAt,
    Date updatedAt,
    UserPlanInfoResponse userPlanInfo,
    List<ObjectId> savedPlans,
    List<CommentResponse> comments
) {

}
