package travel.travelapplication.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SessionUser implements Serializable {

  private Long placeId;
  private String name;
  private String address;
  private Long predict_lasso;

  @Builder
  public SessionUser(Long placeId, String name, String address, Long predict_lasso) {
    this.placeId = placeId;
    this.name = name;
    this.address = address;
    this.predict_lasso = predict_lasso;
  }
}
