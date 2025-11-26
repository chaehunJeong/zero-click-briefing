package kr.co.reo.api_sero_click.api.service;

import kr.co.reo.api_sero_click.api.OllamaChatClient;
import kr.co.reo.api_sero_click.model.BriefingResponse;
import kr.co.reo.api_sero_click.model.PatternData;
import kr.co.reo.api_sero_click.model.WeatherResponse;
import kr.co.reo.api_sero_click.repository.PatternRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AIBriefingService {

  private final PatternRepository patternRepository;
  private final WeatherService weatherService;
  private final TrafficService trafficService;
  private final OllamaChatClient ollamaClient;

  public AIBriefingService(
    PatternRepository patternRepository,
    WeatherService weatherService,
    TrafficService trafficService,
    OllamaChatClient ollamaClient) {
    this.patternRepository = patternRepository;
    this.weatherService = weatherService;
    this.trafficService = trafficService;
    this.ollamaClient = ollamaClient;
  }

  /**
   * AI 기반 브리핑 생성
   */
  public BriefingResponse generateBriefing(String userId) {
    LocalDateTime now = LocalDateTime.now();
    int currentHour = now.getHour();
    int currentDayOfWeek = now.getDayOfWeek().getValue();

    // 1. 사용자의 과거 패턴 데이터 조회 (최근 30일)
    LocalDateTime thirtyDaysAgo = now.minusDays(30);
    List<PatternData> recentPatterns = patternRepository
      .findByUserIdAndCreatedAtAfter(userId, thirtyDaysAgo);

    // 2. 패턴 분석
    boolean shouldNotify = analyzePattern(recentPatterns, currentHour, currentDayOfWeek);

    // 3. 날씨 및 교통 정보 수집 (패턴 데이터 없어도 브리핑 생성)
    String briefingText = "";

    // 최근 패턴에서 평균 위치 계산 (없으면 서울 기본값)
    double avgLat = recentPatterns.stream()
      .mapToDouble(PatternData::getLatitude)
      .average()
      .orElse(37.5665);
    double avgLon = recentPatterns.stream()
      .mapToDouble(PatternData::getLongitude)
      .average()
      .orElse(126.9780);

    // 날씨 및 교통 정보
    WeatherResponse weather = weatherService.getWeatherInfo(avgLat, avgLon);
    var traffic = trafficService.getTrafficInfo(avgLat, avgLon);

    // 패턴 데이터가 없어도 기본 브리핑 생성
    if (shouldNotify && !recentPatterns.isEmpty()) {
      // AI로 자연스러운 브리핑 생성 (패턴 기반 + 교통 정보)
      String system = """
                너는 '아침 브리핑 AI'다.
                사용자의 출발 시간을 예측하고 날씨, 교통 정보를 간결하게 요약해줘.
                교통 정보는 자동차와 대중교통을 모두 포함해서 알려줘.
                친근하고 자연스러운 한국어로 2-3문장으로 답변해.
                """;
      String user = String.format(
        """
        현재 시각: %s시, 기온: %.1f℃, 체감온도: %.1f℃, 날씨: %s
        🚗 자동차: %d분 소요 (%s, %s)
        🚇 대중교통: %d분 소요 (%s, %s, 환승 %d회)
        예상 출발 30분 전
        """,
        currentHour, weather.getTemperature(), weather.getFeelsLike(), weather.getDescription(),
        traffic.getCarDuration(), traffic.getCarCongestion(), traffic.getCarRoute(),
        traffic.getTransitDuration(), traffic.getTransitCongestion(), traffic.getTransitRoute(), traffic.getTransfers()
      );

      briefingText = ollamaClient.chat(system, user);
    } else {
      // 패턴 데이터 없을 때 기본 브리핑 생성
      String system = """
                너는 '친절한 날씨 안내 AI'다.
                현재 날씨 정보를 바탕으로 간단하고 따뜻한 인사와 함께
                외출 시 유의사항을 1-2문장으로 알려줘.
                """;
      String user = String.format(
        "현재 시각: %s시, 기온: %.1f℃, 체감온도: %.1f℃, 날씨: %s",
        currentHour, weather.getTemperature(), weather.getFeelsLike(), weather.getDescription()
      );

      briefingText = ollamaClient.chat(system, user);
    }

    // 4. 판단 결과 반환
    return new BriefingResponse(
      shouldNotify,
      shouldNotify ? "평소 출발 시간 30분 전입니다" : "아직 출발 시간이 아닙니다",
      0.85,
      String.format("%02d:30", currentHour + 1),
      briefingText
    );
  }

  /**
   * 패턴 분석 로직: "집에서 나가는 시간" 감지
   *
   * 핵심 아이디어:
   * 1. 사용자의 "집" 위치 파악 (밤~새벽에 가장 자주 있는 위치)
   * 2. 평일 오전에 "집에서 멀어지는 시점" 찾기
   * 3. 그 시간 30분 전에 알림
   */
  private boolean analyzePattern(List<PatternData> patterns, int currentHour, int dayOfWeek) {
    if (patterns.isEmpty() || patterns.size() < 10) {
      return false;  // 최소 10개 데이터 필요
    }

    // 1. 집 위치 파악 (밤 10시~새벽 6시 사이 가장 자주 있는 위치)
    List<PatternData> nightPatterns = patterns.stream()
      .filter(p -> p.getHour() >= 22 || p.getHour() <= 6)
      .toList();

    if (nightPatterns.isEmpty()) {
      return false;
    }

    // 집 위치 평균 계산
    double homeLat = nightPatterns.stream()
      .mapToDouble(PatternData::getLatitude)
      .average()
      .orElse(0);
    double homeLon = nightPatterns.stream()
      .mapToDouble(PatternData::getLongitude)
      .average()
      .orElse(0);

    // 2. 평일 오전(6~10시) 패턴에서 "집에서 멀어지는 시점" 찾기
    List<PatternData> weekdayMorningPatterns = patterns.stream()
      .filter(p -> p.getDayOfWeek() >= 1 && p.getDayOfWeek() <= 5)  // 평일
      .filter(p -> p.getHour() >= 6 && p.getHour() <= 10)  // 오전 6~10시
      .toList();

    if (weekdayMorningPatterns.isEmpty()) {
      return false;
    }

    // 3. 출발 시간 추정: 집에서 500m 이상 떨어진 시점의 평균
    List<Integer> departureHours = weekdayMorningPatterns.stream()
      .filter(p -> {
        double distance = calculateDistance(homeLat, homeLon,
                                           p.getLatitude(), p.getLongitude());
        return distance > 0.5;  // 500m 이상 떨어진 경우
      })
      .map(PatternData::getHour)
      .toList();

    if (departureHours.isEmpty()) {
      return false;
    }

    // 평균 출발 시간 계산
    double avgDepartureHour = departureHours.stream()
      .mapToInt(Integer::intValue)
      .average()
      .orElse(8.5);  // 기본값 8시 30분

    // 4. 알림 판단: 현재가 평일이고, 평균 출발 시간 30분~1시간 전이면 알림
    boolean isWeekday = dayOfWeek >= 1 && dayOfWeek <= 5;
    boolean isBeforeDeparture = currentHour >= (avgDepartureHour - 1)
                             && currentHour < avgDepartureHour;

    // 5. 최소 3번 이상 동일한 시간대에 출발한 패턴이 있어야 신뢰도 있음
    int frequencyAtThisHour = (int) departureHours.stream()
      .filter(h -> Math.abs(h - avgDepartureHour) <= 1)  // ±1시간 범위
      .count();

    return isWeekday && isBeforeDeparture && frequencyAtThisHour >= 3;
  }

  /**
   * 두 좌표 간 거리 계산 (단위: km)
   * Haversine formula 사용
   */
  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final double R = 6371;  // 지구 반지름 (km)

    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
             + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
             * Math.sin(dLon / 2) * Math.sin(dLon / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }
}