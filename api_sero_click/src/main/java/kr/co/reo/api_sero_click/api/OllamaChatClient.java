// src/main/java/com/example/zcapi/OllamaChatClient.java
package kr.co.reo.api_sero_click.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Component
public class OllamaChatClient {
  private final RestClient http;
  private final String model;

  public OllamaChatClient(
    @Value("${app.ollama.base-url}") String baseUrl,
    @Value("${app.ollama.model}") String model) {
    this.http = RestClient.builder().baseUrl(baseUrl).build();
    this.model = model;
  }

  public String chat(String system, String user) {
    var body = Map.of(
      "model", model,
      "messages", List.of(
        Map.of("role", "system", "content", system),
        Map.of("role", "user", "content", user)
      ),
      "stream", false
    );
    var res = http.post()
      .uri("/api/chat")
      .contentType(MediaType.APPLICATION_JSON)
      .body(body)
      .retrieve()
      .body(Map.class);

    if (res == null) return "";
    Object message = res.get("message");
    if (message instanceof Map<?,?> m) {
      Object content = m.get("content");
      return content == null ? "" : content.toString();
    }
    return "";
  }
}