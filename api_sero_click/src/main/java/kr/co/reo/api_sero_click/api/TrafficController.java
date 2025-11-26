
package kr.co.reo.api_sero_click.api;

import kr.co.reo.api_sero_click.api.service.TrafficService;
import kr.co.reo.api_sero_click.model.TrafficResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/traffic")
@CrossOrigin(origins = "*")
public class TrafficController {

  private final TrafficService trafficService;

  public TrafficController(TrafficService trafficService) {
    this.trafficService = trafficService;
  }

  /**
   * 교통 정보 조회
   */
  @GetMapping
  public TrafficResponse getTraffic(
    @RequestParam double lat,
    @RequestParam double lon) {
    return trafficService.getTrafficInfo(lat, lon);
  }
}