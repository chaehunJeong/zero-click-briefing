package kr.co.reo.api_sero_click.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OllamaClient {
  private final RestClient http;
  private final String model;

  public OllamaClient(
    @Value("${app.ollama.base-url}") String baseUrl,
    @Value("${app.ollama.model}") String model) {
    this.http = RestClient.builder().baseUrl(baseUrl).build();
    this.model = model;
  }

  public String generate(String prompt) {
    var req = Map.of("model", model, "prompt", prompt, "stream", false);
    var res = http.post()
      .uri("/api/generate")
      .contentType(MediaType.APPLICATION_JSON)
      .body(req)
      .retrieve()
      .body(Map.class);
    if (res == null) return "";
    Object response = res.get("response");
    return response == null ? "" : response.toString();
  }
}
