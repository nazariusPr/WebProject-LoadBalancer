package com.example.nazarius.service.impl;

import com.example.nazarius.service.BalancingAlgorithmService;
import com.example.nazarius.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@AllArgsConstructor
public class LoadBalancerImpl implements LoadBalancerService {
  private final BalancingAlgorithmService balancingAlgorithmService;
  private final RestTemplate restTemplate;

  public ResponseEntity<?> forwardRequest(HttpServletRequest request, String body) {
    HttpMethod method = getHttpMethod(request);
    HttpHeaders headers = getHttpHeaders(request);
    URI targetUri = getTargetUri(request);

    HttpEntity<Object> requestEntity =
        (body == null || body.isEmpty())
            ? new HttpEntity<>(headers)
            : new HttpEntity<>(body, headers);

    return restTemplate.exchange(targetUri, method, requestEntity, String.class);
  }

  private HttpMethod getHttpMethod(HttpServletRequest request) {
    return HttpMethod.valueOf(request.getMethod());
  }

  private HttpHeaders getHttpHeaders(HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.add(headerName, request.getHeader(headerName));
    }
    return headers;
  }

  private URI getTargetUri(HttpServletRequest request) {
    URI uri =
        UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
            .query(request.getQueryString())
            .build()
            .toUri();
    String targetUrl = balancingAlgorithmService.getServerUrl();

    log.info("**/ Request sent to " + targetUrl);
    log.info("**/ URI " + uri);

    return UriComponentsBuilder.fromHttpUrl(targetUrl)
        .path(uri.getPath())
        .query(uri.getQuery())
        .build()
        .toUri();
  }
}
