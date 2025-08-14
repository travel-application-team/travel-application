package travel.travelapplication.userplan.dto;

public record SendUserPlanInfoRequest(
    String email,
    String city,
    String district,
    long period
){

}
