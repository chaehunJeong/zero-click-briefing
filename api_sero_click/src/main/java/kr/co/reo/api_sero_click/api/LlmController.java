package kr.co.reo.api_sero_click.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/llm")
public class LlmController {
  private final OllamaClient ollama;

  public LlmController(OllamaClient ollama) { this.ollama = ollama; }

  @PostMapping(value="/rewrite", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String,String> rewrite(@RequestBody Map<String,String> body) {
    var text = body.getOrDefault("text", "");
    var prompt = """
            항상 한국어로 답하세요. 아래 문장을 1~2문장으로 간결하고 자연스럽게 다듬어주세요.
            숫자/시간/장소/단위는 보존하세요.
            ----
            %s
            """.formatted(text);
    var out = ollama.generate(prompt);
    return Map.of("text", out.isBlank() ? text : out.trim());
  }
}
