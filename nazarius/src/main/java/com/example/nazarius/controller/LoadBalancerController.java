package com.example.nazarius.controller;

import com.example.nazarius.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

@RestController
@AllArgsConstructor
public class LoadBalancerController {
    private final LoadBalancerService loadBalancerService;

    @RequestMapping("/**")
    public ResponseEntity<?> handleRequest(HttpServletRequest request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        URI uri = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString()).query(request.getQueryString()).build().toUri();

        // Copy headers
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }

        // Read the body only if it's a method that might include one
        String body = null;
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            try {
                body = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Forward request through LoadBalancerService
        return loadBalancerService.forwardRequest(method, uri, headers, body);
    }

}
