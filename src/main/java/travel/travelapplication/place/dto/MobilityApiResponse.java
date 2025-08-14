package travel.travelapplication.place.dto;

// Kakako Mobility Response
public record MobilityApiResponse (
    String name,
    Double x,
    Double y,
    int distance,
    int duration
){

}
