package com.example.nazarius.service.impl;

import com.example.nazarius.service.BalancingAlgorithmService;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RandomSelectionImpl implements BalancingAlgorithmService {
  private final List<String> serverUrls;
  private final Random random = new Random();

  @Override
  public String getServerUrl() {
    int index = random.nextInt(serverUrls.size());
    return serverUrls.get(index);
  }
}
