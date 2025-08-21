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

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER001", "User not found"),

  // UserPlan
  USER_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_PLAN001", "UserPlan not found");

  private final HttpStatus status;
  private final String code;
  private final String message;

}
