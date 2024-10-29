package com.example.nazarius.service.impl;

import com.example.nazarius.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
public class LoadBalancerImpl implements LoadBalancerService {
    private final List<String> serverUrls;
    private final RestTemplate restTemplate;
    private final AtomicInteger counter = new AtomicInteger(0);

    public ResponseEntity<?> forwardRequest(HttpServletRequest request) {
        HttpMethod method = getHttpMethod(request);
        HttpHeaders headers = getHttpHeaders(request);
        URI targetUri = getTargetUri(request);
        String body = getHttpBody(request, method);

        System.out.println("Forwarding request:");
        System.out.println("Method: " + method);
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + body);

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
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private URI getTargetUri(HttpServletRequest request) {
        URI uri =
                UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
                        .query(request.getQueryString())
                        .build()
                        .toUri();
        String targetUrl = serverUrls.get(counter.getAndIncrement() % serverUrls.size());
        return UriComponentsBuilder.fromHttpUrl(targetUrl)
                .path(uri.getPath())
                .query(uri.getQuery())
                .build()
                .toUri();
    }

    private String getHttpBody(HttpServletRequest request, HttpMethod method) {
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return body.toString();
        }
        return null;
    }
}
