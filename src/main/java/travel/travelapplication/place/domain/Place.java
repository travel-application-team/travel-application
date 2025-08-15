package travel.travelapplication.place.domain;

import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import travel.travelapplication.place.constant.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "Place")
@Getter
@NoArgsConstructor
public class Place {

    @Id
    private ObjectId id;

    private String placeId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    @DBRef
    private ProvCity provCity;

    @DBRef
    private CityCountyDistrict district;

    @DBRef
    private List<Tag> tags = new ArrayList<>();

    public Place(String name, Category category, ProvCity provCity, CityCountyDistrict district, List<Tag> tags) {
        this.name = name;
        this.category = category;
        this.provCity = provCity;
        this.district = district;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if(this==o) return true;
        if(o==null || getClass()!=o.getClass()) return false;
        Place place=(Place) o;
        return Objects.equals(placeId, place.placeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId);
    }
}
