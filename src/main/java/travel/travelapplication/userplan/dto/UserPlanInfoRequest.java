package travel.travelapplication.userplan.dto;

import java.time.LocalDate;
import lombok.Builder;

import travel.travelapplication.userplan.constant.Status;
import travel.travelapplication.userplan.domain.UserPlan;

@Builder
public record UserPlanInfoRequest(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    Long budget,
    String city,
    String district,
    Status status) {

  public UserPlan toEntity() {
    return UserPlan.builder()
        .name(name)
        .startDate(startDate)
        .endDate(endDate)
        .budget(budget)
        .city(city)
        .district(district)
        .status(status)
        .build();
  }
}
