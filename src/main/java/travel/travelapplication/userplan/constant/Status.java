package travel.travelapplication.userplan.constant;

public enum Status {
  PUBLIC("공개"), PRIVATE("비공개");

  private final String description;

  Status(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
