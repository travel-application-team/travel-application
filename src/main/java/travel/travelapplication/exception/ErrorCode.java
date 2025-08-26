package travel.travelapplication.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // Common
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON000", "서버 에러, 관리자에게 연락해주세요."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON001", "잘못된 값을 입력하였습니다"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON002", "요청한 리소스를 찾을 수 없습니다."),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON003", "허용되지 않은 HTTP 메서드입니다."),

  // Auth
  PRINCIPAL_NAME_EMPTY(HttpStatus.UNAUTHORIZED, "AUTH001", "인증 정보가 없습니다."),

  //Place
  PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE001", "장소를 찾을 수 없습니다."),
  CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE002", "해당 도시를 찾을 수 없습니다."),

  // Tag
  INVALID_TAG_SELECTION(HttpStatus.BAD_REQUEST, "TAG001","태그 선택이 유효하지 않습니다."),

  // Plan
  PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN001", "Plan이 없습니다."),

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER001", "사용자를 찾을 수 없습니다."),
  USER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "USER002", "인증되지 않은 사용자입니다. 로그인하세요."),

  // UserPlan
  SAVED_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_PLAN001", "SavedPlan이 없습니다."),
  USER_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_PLAN002", "UserPlan이 없습니다."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;

}
