package travel.travelapplication.place.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiRequest { // Kakao Mobility Request

    private Location origin;
    private Location destination;
    private List<Location> waypoints;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Location {
        private String name;
        private Double x;
        private Double y;

        public Location(String name, Double x, Double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
}
