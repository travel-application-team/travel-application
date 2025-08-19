package travel.travelapplication.userplan.presentation;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import travel.travelapplication.userplan.application.SavedPlanService;
import travel.travelapplication.userplan.domain.SavedPlan;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/saved-plans")
public class SavedPlanController {

  private final SavedPlanService savedPlanService;

  @GetMapping
  public List<SavedPlan> getSavedPlan() {
    return savedPlanService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<SavedPlan> findSavedPlan(@RequestParam(name = "id") ObjectId id) {
    SavedPlan savedPlan = savedPlanService.findById(id);
    return ResponseEntity.ok(savedPlan);
  }

  @DeleteMapping("/{id}")
  public void deleteSavedPlan(@PathVariable(name = "id") ObjectId id) {
    savedPlanService.deleteById(id);
  }
}
