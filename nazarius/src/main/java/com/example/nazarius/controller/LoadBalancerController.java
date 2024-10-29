package com.example.nazarius.controller;

import com.example.nazarius.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class LoadBalancerController {
    private final LoadBalancerService loadBalancerService;

    @RequestMapping("/**")
    public ResponseEntity<?> handleRequest(HttpServletRequest request) {
        log.info("**/ processing request");
        return loadBalancerService.forwardRequest(request);
    }

}
