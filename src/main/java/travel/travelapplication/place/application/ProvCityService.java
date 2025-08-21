package travel.travelapplication.place.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travelapplication.place.constant.City;
import travel.travelapplication.place.domain.ProvCity;
import travel.travelapplication.place.exception.CityNotFoundException;
import travel.travelapplication.place.repository.ProvCityRepository;

@Service
@RequiredArgsConstructor
public class ProvCityService {

  private final ProvCityRepository provCityRepository;

  //위치 괜찮은지 논의 필요
  public List<String> getDistrictsByCity(City city) {
    String cityName = city.getName();
    List<String> districts = new ArrayList<>();
    if (provCityRepository.findByName(cityName).isPresent()) {
      ProvCity provCity = provCityRepository.findByName(cityName).get();
      districts = provCity.getDistricts();
    } else {
      throw new CityNotFoundException(cityName);
    }

    return districts;
  }
}
