package kr.co.reo.api_sero_click.api;

import kr.co.reo.api_sero_click.api.service.AIBriefingService;
import kr.co.reo.api_sero_click.model.BriefingResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/briefing")
@CrossOrigin(origins = "*")
public class ZeroClickBriefingController {

  private final AIBriefingService briefingService;

  public ZeroClickBriefingController(AIBriefingService briefingService) {
    this.briefingService = briefingService;
  }

  /**
   * AI 기반 브리핑 생성
   */
  @GetMapping("/{userId}")
  public BriefingResponse getBriefing(@PathVariable String userId) {
    return briefingService.generateBriefing(userId);
  }
}