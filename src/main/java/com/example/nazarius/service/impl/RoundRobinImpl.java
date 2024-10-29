package com.example.nazarius.service.impl;

import com.example.nazarius.service.BalancingAlgorithmService;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoundRobinImpl implements BalancingAlgorithmService {
  private final List<String> serverUrls;
  private final AtomicInteger counter = new AtomicInteger(0);

  @Override
  public String getServerUrl() {
    int index = counter.getAndUpdate(current -> (current + 1) % serverUrls.size());
    return serverUrls.get(index);
  }
}
