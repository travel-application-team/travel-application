package travel.travelapplication.userplan.repository;

import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

import travel.travelapplication.userplan.domain.UserPlan;

public interface UserPlanRepository extends MongoRepository<UserPlan, ObjectId> {

}
