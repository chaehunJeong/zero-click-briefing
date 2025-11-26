package kr.co.reo.api_sero_click.api;

import kr.co.reo.api_sero_click.api.service.RateLimiterService;
import kr.co.reo.api_sero_click.api.service.WeatherService;
import kr.co.reo.api_sero_click.model.WeatherResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

  private final WeatherService weatherService;

  public WeatherController(WeatherService weatherService) {
    this.weatherService = weatherService;
  }

  @GetMapping
  public WeatherResponse getWeather(
    @RequestParam double lat,
    @RequestParam double lon) {
    return weatherService.getWeatherInfo(lat, lon);
  }

  @GetMapping("/city/{cityName}")
  public WeatherResponse getWeatherByCity(@PathVariable String cityName) {
    return weatherService.getWeatherByCity(cityName);
  }

  @GetMapping("/summary")
  public Map<String, String> getWeatherSummary(
    @RequestParam double lat,
    @RequestParam double lon) {
    String summary = weatherService.summaryByLocation(lat, lon);
    return Map.of("summary", summary);
  }

  /**
   * Rate Limit 상태 조회 (LLM 기반 WeatherService는 rate limit 없음)
   * 예: GET /api/weather/rate-limit
   */
  @GetMapping("/rate-limit")
  public Map<String, String> getRateLimitStatus() {
    return Map.of(
      "status", "unlimited",
      "message", "LLM-based weather service has no rate limits"
    );
  }
}