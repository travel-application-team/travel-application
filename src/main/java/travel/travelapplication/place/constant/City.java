package travel.travelapplication.place.constant;

public enum City {
  SEOUL("서울"),
  BUSAN("부산"),
  INCHEON("인천"),
  DAEGU("대구"),
  DAEJEON("대전"),
  ULSAN("울산"),
  GYEONGGI("경기"),
  GANGWON("강원"),
  CHUNGBUK("충북"),
  CHUNGNAM("충남"),
  JEONBUK("전북"),
  JEONNAM("전남"),
  GYEONGBUK("경북"),
  GYEONGNAM("경남"),
  JEJU("제주");

  private final String name;

  City(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
