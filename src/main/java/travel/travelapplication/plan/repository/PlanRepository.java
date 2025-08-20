package travel.travelapplication.plan.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.userplan.domain.UserPlan;

public interface PlanRepository extends MongoRepository<Plan, ObjectId> {

  Plan findByUserPlan(UserPlan userPlan);

  @Query("{ 'userPlan.places.name': { $regex: ?0, $options: 'i' } }")
  List<Plan> findByPlaceName(String placeName);
}
