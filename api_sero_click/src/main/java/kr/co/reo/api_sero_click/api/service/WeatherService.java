package kr.co.reo.api_sero_click.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.reo.api_sero_click.api.OllamaChatClient;
import kr.co.reo.api_sero_click.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * LLM 기반 날씨 정보 생성 서비스
 * 실제 API 호출 대신 LLM이 계절과 시간대를 고려한 현실적인 날씨 데이터를 생성합니다.
 */
@Slf4j
@Service
public class WeatherService {

  private final OllamaChatClient llm;
  private final ObjectMapper objectMapper;

  public WeatherService(OllamaChatClient llm) {
    this.llm = llm;
    this.objectMapper = new ObjectMapper();
    log.info("LLM-based WeatherService initialized (zero API cost!)");
  }

  public String summarySeoul() {
    return summaryByCity("Seoul");
  }

  public String summaryByCity(String city) {
    WeatherResponse weather = getWeatherInfo(37.5665, 126.9780);
    return String.format("기온 %.0f℃, 체감온도 %.0f℃, 날씨 %s",
      weather.getTemperature(), weather.getFeelsLike(), weather.getDescription());
  }

  public String summaryByLocation(double lat, double lon) {
    WeatherResponse weather = getWeatherInfo(lat, lon);
    return String.format("기온 %.0f℃, 체감온도 %.0f℃, 날씨 %s",
      weather.getTemperature(), weather.getFeelsLike(), weather.getDescription());
  }

  /**
   * LLM을 사용하여 현실적인 날씨 정보 생성
   */
  public WeatherResponse getWeatherInfo(double lat, double lon) {
    try {
      LocalDateTime now = LocalDateTime.now();
      int month = now.getMonthValue();
      int hour = now.getHour();
      int day = now.getDayOfMonth();

      // 계절별 실제 서울 기온 범위
      String seasonGuide = switch (month) {
        case 12, 1, 2 -> "겨울철: -5~5도, 맑음/구름많음 위주";
        case 3, 4, 5 -> "봄철: 10~20도, 화창한 날씨 많음";
        case 6, 7, 8 -> "여름철: 25~35도, 습하고 더움, 비 자주";
        case 9, 10 -> "가을철: 15~23도, 맑고 선선함";
        case 11 -> "늦가을: 10~15도, 서늘하고 건조함";
        default -> "10~15도";
      };

      String prompt = String.format("""
        현재 날짜는 %d월 %d일 %d시입니다.
        위치는 위도 %.4f, 경도 %.4f (서울 지역)입니다.

        **계절 정보**: %s

        현실적인 날씨 정보를 생성하세요. 반드시 JSON 형식으로만 응답하세요:
        {
          "condition": "Clear 또는 Clouds 또는 Rain 또는 Snow",
          "temperature": 위 계절 범위 내의 기온(숫자만),
          "feelsLike": 체감온도(실제 기온 ±3도),
          "humidity": 습도(40~70),
          "description": "맑음 또는 구름많음 또는 흐림 또는 비 또는 눈"
        }
        """, month, day, hour, lat, lon, seasonGuide);

      String system = "당신은 날씨 데이터 생성 전문가입니다. JSON 형식으로만 정확하게 응답하세요.";
      String jsonResponse = llm.chat(system, prompt);

      // JSON 파싱
      jsonResponse = extractJson(jsonResponse);
      Map<String, Object> weatherData = objectMapper.readValue(jsonResponse, Map.class);

      String condition = String.valueOf(weatherData.get("condition"));
      double temperature = parseDouble(weatherData.get("temperature"));
      double feelsLike = parseDouble(weatherData.get("feelsLike"));
      int humidity = parseInt(weatherData.get("humidity"));
      String description = String.valueOf(weatherData.get("description"));

      log.info("LLM generated weather: {}°C, {}", temperature, description);
      return new WeatherResponse(condition, temperature, feelsLike, humidity, description);

    } catch (Exception e) {
      log.error("Failed to generate weather with LLM, using fallback", e);
      return getFallbackWeather();
    }
  }

  public WeatherResponse getWeatherByCity(String city) {
    return getWeatherInfo(37.5665, 126.9780);
  }

  /**
   * JSON 추출 (LLM이 추가 텍스트를 포함할 경우 대비)
   */
  private String extractJson(String response) {
    response = response.trim();
    int start = response.indexOf('{');
    int end = response.lastIndexOf('}');
    if (start >= 0 && end > start) {
      return response.substring(start, end + 1);
    }
    return response;
  }

  private double parseDouble(Object value) {
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    try {
      return Double.parseDouble(String.valueOf(value));
    } catch (NumberFormatException e) {
      return 5.0; // 11월 기본값
    }
  }

  private int parseInt(Object value) {
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    try {
      return Integer.parseInt(String.valueOf(value));
    } catch (NumberFormatException e) {
      return 60; // 기본 습도
    }
  }

  /**
   * LLM 실패 시 폴백 날씨 데이터
   */
  private WeatherResponse getFallbackWeather() {
    LocalDateTime now = LocalDateTime.now();
    int month = now.getMonthValue();

    // 11월은 쌀쌀함
    double temp = 5.0;
    double feelsLike = 2.0;

    return new WeatherResponse("Clouds", temp, feelsLike, 60, "구름많음");
  }
}