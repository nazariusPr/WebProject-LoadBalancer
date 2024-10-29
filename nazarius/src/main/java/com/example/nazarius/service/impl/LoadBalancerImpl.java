package com.example.nazarius.service.impl;

import com.example.nazarius.service.LoadBalancerService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadBalancerImpl implements LoadBalancerService {
    private final List<String> serverUrls = List.of("http://localhost:8081");
    private final AtomicInteger counter = new AtomicInteger(0);

    public ResponseEntity<?> forwardRequest(HttpMethod method, URI uri, HttpHeaders headers, Object body) {
        RestTemplate restTemplate = new RestTemplate();

        // Configure RestTemplate to not throw exceptions on 4xx/5xx responses
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response){
                return false; // Treat all responses as non-errors
            }

            @Override
            public void handleError(ClientHttpResponse response){
                // No-op: No handling needed since hasError is set to false
            }
        });

        // Select server using round-robin
        String targetUrl = serverUrls.get(counter.getAndIncrement() % serverUrls.size());
        URI targetUri = UriComponentsBuilder.fromHttpUrl(targetUrl).path(uri.getPath()).query(uri.getQuery()).build().toUri();

        System.out.println(targetUrl);
        // Create HttpEntity based on presence of body
        HttpEntity<Object> requestEntity = (body == null) ? new HttpEntity<>(headers) : new HttpEntity<>(body, headers);

        // Forward request and get the response
        ResponseEntity<?> response = restTemplate.exchange(targetUri, method, requestEntity, String.class);

        System.out.println(response);

        return response;
    }

}
