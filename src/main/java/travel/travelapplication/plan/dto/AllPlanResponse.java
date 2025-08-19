package travel.travelapplication.plan.dto;

import java.util.Date;
import org.bson.types.ObjectId;

public record AllPlanResponse(
    ObjectId planId,
    String name,
    Date createdAt
) {

}
