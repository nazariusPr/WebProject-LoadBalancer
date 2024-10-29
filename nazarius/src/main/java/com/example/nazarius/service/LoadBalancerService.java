package com.example.nazarius.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public interface LoadBalancerService {
    ResponseEntity<?> forwardRequest(HttpMethod method, URI uri, HttpHeaders headers, Object object);
}
