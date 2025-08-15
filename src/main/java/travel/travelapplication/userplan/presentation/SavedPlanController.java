package travel.travelapplication.userplan.presentation;

import jakarta.transaction.Transactional;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import travel.travelapplication.userplan.application.SavedPlanService;
import travel.travelapplication.userplan.domain.SavedPlan;

@Transactional
@RestController
@RequestMapping("/saved-plan")
public class SavedPlanController {

  private SavedPlanService service;

  @GetMapping
  public List<SavedPlan> getSavedPlan() {
    return service.findAllSavedPlan();
  }

  @GetMapping("/{id}")
  public ResponseEntity<SavedPlan> findSavedPlan(@RequestParam(name = "id") ObjectId id) {
    SavedPlan savedPlan = service.findById(id);
    return ResponseEntity.ok(new SavedPlan(savedPlan.getPlan()));
  }

  @DeleteMapping("/{id}")
  public void deleteSavedPlan(@RequestParam(name = "id") ObjectId id) {
  }
}
