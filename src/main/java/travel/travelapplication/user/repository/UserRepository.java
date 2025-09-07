package travel.travelapplication.user.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import travel.travelapplication.user.domain.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);

    boolean existsByName(String name);

    Optional<User> findByRefreshToken(String refreshToken);
}
