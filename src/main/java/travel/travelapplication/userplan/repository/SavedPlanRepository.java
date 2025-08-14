package travel.travelapplication.userplan.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import travel.travelapplication.userplan.domain.SavedPlan;

public interface SavedPlanRepository extends MongoRepository<SavedPlan, ObjectId> {
}
