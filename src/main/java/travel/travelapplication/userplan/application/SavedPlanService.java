package travel.travelapplication.userplan.application;

import java.util.List;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.userplan.repository.SavedPlanRepository;
import travel.travelapplication.userplan.domain.SavedPlan;

@AllArgsConstructor
@Service
public class SavedPlanService {

  private SavedPlanRepository repository;

  public List<SavedPlan> findAllSavedPlan() {
    return repository.findAll();
  }


  public SavedPlan findById(ObjectId id) {
    return repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(id + "not found"));
  }
}
