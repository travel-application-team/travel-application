package travel.travelapplication.plan.dto;

import java.util.Date;
import org.bson.types.ObjectId;

public record PlanListItemResponse(
    ObjectId planId,
    String name,
    Date createdAt
) {

}
