package com.example.nazarius.config;

import static com.example.nazarius.constants.Server.SERVER_URLS;

import com.example.nazarius.service.BalancingAlgorithmService;
import com.example.nazarius.service.impl.RoundRobinImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class LoadBalancerConfig {

  @Bean
  public RestTemplate restTemplate() {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory(httpClient);

    RestTemplate restTemplate = new RestTemplate(requestFactory);
    restTemplate.setErrorHandler(
        new ResponseErrorHandler() {
          @Override
          public boolean hasError(ClientHttpResponse response) {
            return false;
          }

          @Override
          public void handleError(ClientHttpResponse response) {
            log.error("Error processing request");
          }
        });

    return restTemplate;
  }

  @Bean
  public BalancingAlgorithmService balancingAlgorithmService() {
    return new RoundRobinImpl(SERVER_URLS);
  }

  /*
  @Bean BalancingAlgorithmService balancingAlgorithmService(){
      return new RandomSelectionImpl(SERVER_URLS);
  }
  */
}
