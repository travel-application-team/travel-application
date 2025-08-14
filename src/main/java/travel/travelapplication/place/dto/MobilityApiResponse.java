package travel.travelapplication.place.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Kakako Mobility Response
public record MobilityApiResponse (
    String name,
    Double x,
    Double y,
    int distance,
    int duration
){

}
