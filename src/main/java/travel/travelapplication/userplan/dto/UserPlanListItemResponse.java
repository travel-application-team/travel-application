package travel.travelapplication.userplan.dto;

import java.util.Date;
import org.bson.types.ObjectId;

public record UserPlanListItemResponse(
    ObjectId id,
    String name,
    Date createdAt
) {

}
