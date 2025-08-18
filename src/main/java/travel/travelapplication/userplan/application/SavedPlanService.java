package travel.travelapplication.userplan.application;

import java.util.List;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.userplan.domain.SavedPlan;
import travel.travelapplication.userplan.repository.SavedPlanRepository;

@AllArgsConstructor
@Service
public class SavedPlanService {

  private SavedPlanRepository savedPlanRepository;

  public List<SavedPlan> findAll() {
    return savedPlanRepository.findAll();
  }

  public SavedPlan findById(ObjectId id) {
    return savedPlanRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(id + "not found"));
  }

  public void deleteById(ObjectId id) {
    savedPlanRepository.deleteById(id);
  }
}
