package com.example.nazarius.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface LoadBalancerService {
    ResponseEntity<?> forwardRequest(HttpServletRequest request, String body);
}
