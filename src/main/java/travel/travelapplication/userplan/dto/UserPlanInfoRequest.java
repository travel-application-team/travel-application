package travel.travelapplication.userplan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record UserPlanInfoRequest (
    String email,
    String city,
    String district,
    long period
){

}
