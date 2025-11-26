package kr.co.reo.api_sero_click.confg;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Webconfig implements WebMvcConfigurer  {

  @Override
  public void addCorsMappings(CorsRegistry reg) {
    reg.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
      .allowedHeaders("*");
  }
}
