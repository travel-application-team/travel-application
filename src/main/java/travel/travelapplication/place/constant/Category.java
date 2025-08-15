package travel.travelapplication.place.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    RESTAURANT("RESTAURANT"), CAFE("CAFE"),
    HOSPITAL("HOSPITAL"), PHARMACY("PHARMACY"),
    PARKING("PARKING"), ETC("ETC");

    private final String key;
}
