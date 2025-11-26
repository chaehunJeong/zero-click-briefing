
package kr.co.reo.api_sero_click.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<?> handleRateLimitExceeded(RateLimitExceededException e) {
    return ResponseEntity
      .status(HttpStatus.TOO_MANY_REQUESTS)
      .body(Map.of(
        "error", "Rate Limit Exceeded",
        "message", e.getMessage(),
        "timestamp", LocalDateTime.now(),
        "status", 429
      ));
  }
}