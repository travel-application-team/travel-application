package travel.travelapplication.userplan.domain;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import travel.travelapplication.plan.domain.Plan;

@Document("SavedPlan")
@Getter
public class SavedPlan {

    @Id
    private ObjectId id;

    @DBRef
    private Plan plan;

    public SavedPlan(Plan plan) {
        this.plan = plan;
    }

}
