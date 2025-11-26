
package kr.co.reo.api_sero_click.api;

import kr.co.reo.api_sero_click.api.service.PatternLearningService;
import kr.co.reo.api_sero_click.model.PatternData;
import kr.co.reo.api_sero_click.repository.PatternRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patterns")
@CrossOrigin(origins = "*")
public class PatternController {

  private final PatternLearningService patternService;
  private final PatternRepository patternRepository;

  public PatternController(PatternLearningService patternService, PatternRepository patternRepository) {
    this.patternService = patternService;
    this.patternRepository = patternRepository;
  }

  /**
   * 사용자 패턴 데이터 저장
   */
  @PostMapping
  public ResponseEntity<String> savePattern(@RequestBody PatternData data) {
    patternService.savePattern(data);
    return ResponseEntity.ok("Pattern saved successfully");
  }

  /**
   * 저장된 패턴 조회
   */
  @GetMapping("/{userId}")
  public ResponseEntity<?> getPatterns(@PathVariable String userId) {
    return ResponseEntity.ok(patternService.getUserPatterns(userId));
  }

  /**
   * 시뮬레이션: 3일간의 출근 패턴 데이터 생성 (테스트용)
   *
   * POST /api/patterns/simulate?userId=test_user
   */
  @PostMapping("/simulate")
  public ResponseEntity<Map<String, Object>> simulatePatterns(@RequestParam String userId) {
    LocalDateTime now = LocalDateTime.now();
    int createdCount = 0;

    // 지난 3일간의 데이터 생성
    for (int dayOffset = 0; dayOffset < 3; dayOffset++) {
      LocalDateTime targetDate = now.minusDays(dayOffset);
      int dayOfWeek = targetDate.getDayOfWeek().getValue();

      // 주말은 건너뛰기 (평일 패턴만)
      if (dayOfWeek == 6 || dayOfWeek == 7) {
        continue;
      }

      // 밤 시간 데이터 (집) - 22:00, 23:00, 01:00, 02:00, 05:00, 06:00
      for (int hour : List.of(22, 23, 1, 2, 5, 6)) {
        PatternData pattern = new PatternData();
        pattern.setUserId(userId);
        pattern.setLatitude(37.5450);  // 집 위치 (강서구)
        pattern.setLongitude(126.8411);
        pattern.setHour(hour);
        pattern.setDayOfWeek(dayOfWeek);
        pattern.setCreatedAt(targetDate.withHour(hour).withMinute(0));
        patternRepository.save(pattern);
        createdCount++;
      }

      // 평일 아침 출발 데이터 - 08:00에 집에서 800m 이동
      PatternData departurePattern = new PatternData();
      departurePattern.setUserId(userId);
      departurePattern.setLatitude(37.5500);  // 집에서 약 600m 떨어진 위치
      departurePattern.setLongitude(126.8480);
      departurePattern.setHour(8);
      departurePattern.setDayOfWeek(dayOfWeek);
      departurePattern.setCreatedAt(targetDate.withHour(8).withMinute(0));
      patternRepository.save(departurePattern);
      createdCount++;
    }

    return ResponseEntity.ok(Map.of(
      "success", true,
      "message", "시뮬레이션 패턴 데이터 생성 완료",
      "userId", userId,
      "createdCount", createdCount,
      "description", "3일간 평일 출근 패턴 (밤 집 위치 + 오전 8시 출발)"
    ));
  }

  /**
   * 모든 패턴 데이터 삭제 (테스트용)
   *
   * DELETE /api/patterns/all?userId=test_user
   */
  @DeleteMapping("/all")
  public ResponseEntity<Map<String, Object>> deleteAllPatterns(@RequestParam String userId) {
    List<PatternData> patterns = patternService.getUserPatterns(userId);
    int deletedCount = patterns.size();
    patternRepository.deleteAll(patterns);

    return ResponseEntity.ok(Map.of(
      "success", true,
      "message", "모든 패턴 데이터 삭제 완료",
      "userId", userId,
      "deletedCount", deletedCount
    ));
  }

  /**
   * 시뮬레이션: 1주일간의 상세 출퇴근 패턴 (발표 시연용)
   *
   * POST /api/patterns/simulate-week?userId=demo_user
   *
   * 월~금: 오전 8시 출근 패턴
   * 주말: 재택 패턴
   */
  @PostMapping("/simulate-week")
  public ResponseEntity<Map<String, Object>> simulateWeekPattern(@RequestParam String userId) {
    LocalDateTime now = LocalDateTime.now();
    int createdCount = 0;
    java.util.List<Map<String, Object>> dailyPatterns = new java.util.ArrayList<>();

    // 지난 7일간의 데이터 생성
    for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
      LocalDateTime targetDate = now.minusDays(dayOffset);
      int dayOfWeek = targetDate.getDayOfWeek().getValue(); // 1=월, 7=일
      String dayName = getDayName(dayOfWeek);
      int dayDataCount = 0;

      boolean isWeekday = (dayOfWeek >= 1 && dayOfWeek <= 5);

      // 밤 시간 데이터 (집) - 매일
      for (int hour : List.of(22, 23, 1, 2, 5, 6)) {
        PatternData pattern = new PatternData();
        pattern.setUserId(userId);
        pattern.setLatitude(37.5450);  // 집 위치
        pattern.setLongitude(126.8411);
        pattern.setHour(hour);
        pattern.setDayOfWeek(dayOfWeek);
        pattern.setCreatedAt(targetDate.withHour(hour).withMinute(0));
        patternRepository.save(pattern);
        createdCount++;
        dayDataCount++;
      }

      if (isWeekday) {
        // 평일: 출근 패턴
        // 07:00 - 집
        PatternData morning7 = new PatternData();
        morning7.setUserId(userId);
        morning7.setLatitude(37.5450);
        morning7.setLongitude(126.8411);
        morning7.setHour(7);
        morning7.setDayOfWeek(dayOfWeek);
        morning7.setCreatedAt(targetDate.withHour(7).withMinute(0));
        patternRepository.save(morning7);
        createdCount++;
        dayDataCount++;

        // 08:00 - 출발 (집에서 600m)
        PatternData departure = new PatternData();
        departure.setUserId(userId);
        departure.setLatitude(37.5500);
        departure.setLongitude(126.8480);
        departure.setHour(8);
        departure.setDayOfWeek(dayOfWeek);
        departure.setCreatedAt(targetDate.withHour(8).withMinute(0));
        patternRepository.save(departure);
        createdCount++;
        dayDataCount++;

        // 09:00 - 회사 도착
        PatternData arrival = new PatternData();
        arrival.setUserId(userId);
        arrival.setLatitude(37.4979);  // 강남역 부근
        arrival.setLongitude(127.0276);
        arrival.setHour(9);
        arrival.setDayOfWeek(dayOfWeek);
        arrival.setCreatedAt(targetDate.withHour(9).withMinute(0));
        patternRepository.save(arrival);
        createdCount++;
        dayDataCount++;

        dailyPatterns.add(Map.of(
          "date", targetDate.toLocalDate().toString(),
          "day", dayName,
          "type", "평일 출근",
          "departure", "08:00 (집에서 출발)",
          "arrival", "09:00 (회사 도착)",
          "dataCount", dayDataCount
        ));
      } else {
        // 주말: 재택 패턴 (집에만 있음)
        dailyPatterns.add(Map.of(
          "date", targetDate.toLocalDate().toString(),
          "day", dayName,
          "type", "주말 재택",
          "note", "하루 종일 집",
          "dataCount", dayDataCount
        ));
      }
    }

    return ResponseEntity.ok(Map.of(
      "success", true,
      "message", "1주일 출근 패턴 시뮬레이션 완료",
      "userId", userId,
      "totalDataCount", createdCount,
      "weekPattern", Map.of(
        "weekdays", "월~금 오전 8시 출발",
        "weekends", "주말 재택",
        "home", "강서구 (37.5450, 126.8411)",
        "office", "강남역 (37.4979, 127.0276)"
      ),
      "dailyBreakdown", dailyPatterns,
      "analysis", Map.of(
        "predictedDepartureTime", "08:00",
        "confidence", "높음 (5일 연속 동일 패턴)",
        "recommendedNotificationTime", "07:30"
      )
    ));
  }

  /**
   * 요일 이름 반환
   */
  private String getDayName(int dayOfWeek) {
    return switch (dayOfWeek) {
      case 1 -> "월요일";
      case 2 -> "화요일";
      case 3 -> "수요일";
      case 4 -> "목요일";
      case 5 -> "금요일";
      case 6 -> "토요일";
      case 7 -> "일요일";
      default -> "알 수 없음";
    };
  }
}