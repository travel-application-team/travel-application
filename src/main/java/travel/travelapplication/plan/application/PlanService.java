package travel.travelapplication.plan.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import travel.travelapplication.dto.plan.CommentDto;
import travel.travelapplication.place.domain.Place;
import travel.travelapplication.plan.domain.Comment;
import travel.travelapplication.plan.domain.Plan;
import travel.travelapplication.plan.repository.CommentRepository;
import travel.travelapplication.user.application.UserService;
import travel.travelapplication.user.domain.User;
import travel.travelapplication.userplan.domain.UserPlan;
import travel.travelapplication.plan.repository.PlanRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class PlanService {
    private final PlanRepository planRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public void save(Plan plan) {
        planRepository.save(plan);
    }

    public Plan findById(ObjectId id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found:" + id));
    }

    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    public boolean saveSelectedPlan(User user, Plan plan) {
        List<Plan> savedPlans = user.getSavedPlans();
        boolean isSaved = savedPlans.stream().anyMatch(savedPlan -> savedPlan.getId().equals(plan.getId()));

        if (!isSaved) {
            savedPlans.add(plan);
        } else {
            savedPlans.removeIf(savedPlan -> savedPlan.getId().equals(plan.getId()));
        }
        userService.updateUserSavedPlans(user, savedPlans);
        return !isSaved;
    }

    public void saveCommentToPlan(Plan plan, CommentDto commentDto) { // 커뮤니티 Plan 댓글 저장 기능 (답글 제외)
        List<Comment> comments = plan.getComments();

        Comment comment=commentDto.toEntity();
        commentRepository.insert(comment);

        comments.add(comment);
        updatePlanComment(plan, comments);
    }

    private void updatePlanComment(Plan plan, List<Comment> comments) {
        Plan updatedPlan=Plan.builder()
                .name(plan.getName())
                .userEmail(plan.getUserEmail())
                .userPlan(plan.getUserPlan())
                .comments(comments)
                .build();

        plan.update(updatedPlan);
        save(plan);
    }

    public List<Plan> searchByPlace(String keyword) {
        List<Plan> plans = planRepository.findAll();
        List<Plan> findPlans = new ArrayList<>();

        for (Plan plan : plans) {
            UserPlan userPlan = plan.getUserPlan();
            if (findPlaces(userPlan, keyword)) {
                findPlans.add(plan);
            }
        }
        return findPlans;
    }

    private boolean findPlaces(UserPlan userPlan, String keyword) {
        List<Place> places = userPlan.getPlaces();

        for (Place place : places) {
            if (place.getName().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public void updatePlanFromUserPlanInfo(Plan plan, UserPlan userPlan) {
        Plan updatedPlan=Plan.builder()
                .name(userPlan.getName())
                .userPlan(userPlan)
                .build();

        plan.update(updatedPlan);
        save(plan);
    }
}
