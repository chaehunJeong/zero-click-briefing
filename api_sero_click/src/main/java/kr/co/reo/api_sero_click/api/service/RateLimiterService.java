package kr.co.reo.api_sero_click.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RateLimiterService {

  // 분당 호출 제한
  private static final int MAX_CALLS_PER_MINUTE = 60;
  // 월간 호출 제한
  private static final int MAX_CALLS_PER_MONTH = 1_000_000;

  // 분당 카운터
  private final ConcurrentHashMap<Long, AtomicInteger> minuteCounter = new ConcurrentHashMap<>();
  // 월간 카운터
  private final ConcurrentHashMap<String, AtomicInteger> monthCounter = new ConcurrentHashMap<>();

  /**
   * API 호출 가능 여부 확인
   * @param apiName API 이름 (예: "weather", "traffic")
   * @return 호출 가능하면 true, 제한 초과시 false
   */
  public boolean allowRequest(String apiName) {
    long currentMinute = Instant.now().getEpochSecond() / 60;
    String currentMonth = getCurrentMonth();

    // 1. 분당 제한 확인
    AtomicInteger minuteCount = minuteCounter.computeIfAbsent(
      currentMinute,
      k -> new AtomicInteger(0)
    );

    if (minuteCount.get() >= MAX_CALLS_PER_MINUTE) {
      log.warn("Rate limit exceeded: {} calls/minute for API: {}",
        MAX_CALLS_PER_MINUTE, apiName);
      return false;
    }

    // 2. 월간 제한 확인
    String monthKey = currentMonth + ":" + apiName;
    AtomicInteger monthCount = monthCounter.computeIfAbsent(
      monthKey,
      k -> new AtomicInteger(0)
    );

    if (monthCount.get() >= MAX_CALLS_PER_MONTH) {
      log.warn("Monthly rate limit exceeded: {} calls/month for API: {}",
        MAX_CALLS_PER_MONTH, apiName);
      return false;
    }

    // 3. 카운터 증가
    minuteCount.incrementAndGet();
    monthCount.incrementAndGet();

    // 4. 오래된 분 단위 카운터 정리 (메모리 절약)
    cleanupOldMinutes(currentMinute);

    log.debug("API call allowed: {} (minute: {}/{}, month: {}/{})",
      apiName, minuteCount.get(), MAX_CALLS_PER_MINUTE,
      monthCount.get(), MAX_CALLS_PER_MONTH);

    return true;
  }

  /**
   * 현재 월 문자열 반환 (예: "2025-11")
   */
  private String getCurrentMonth() {
    Instant now = Instant.now();
    return String.format("%tY-%tm", now, now);
  }

  /**
   * 2분 이상 지난 카운터 정리
   */
  private void cleanupOldMinutes(long currentMinute) {
    minuteCounter.entrySet().removeIf(entry ->
      entry.getKey() < currentMinute - 2
    );
  }

  /**
   * 현재 사용량 조회
   */
  public RateLimitStatus getStatus(String apiName) {
    long currentMinute = Instant.now().getEpochSecond() / 60;
    String currentMonth = getCurrentMonth();

    int minuteUsage = minuteCounter.getOrDefault(currentMinute, new AtomicInteger(0)).get();
    int monthUsage = monthCounter.getOrDefault(currentMonth + ":" + apiName, new AtomicInteger(0)).get();

    return new RateLimitStatus(
      minuteUsage,
      MAX_CALLS_PER_MINUTE,
      monthUsage,
      MAX_CALLS_PER_MONTH,
      MAX_CALLS_PER_MINUTE - minuteUsage,
      MAX_CALLS_PER_MONTH - monthUsage
    );
  }

  /**
   * Rate Limit 상태 정보
   */
  public record RateLimitStatus(
    int minuteUsage,
    int minuteLimit,
    int monthUsage,
    int monthLimit,
    int minuteRemaining,
    int monthRemaining
  ) {}
}