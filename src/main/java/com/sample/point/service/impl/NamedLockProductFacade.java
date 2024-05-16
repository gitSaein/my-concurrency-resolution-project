package com.sample.point.service.impl;

import com.sample.point.repository.ProductLockRepository;
import com.sample.point.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockProductFacade {
  private final ProductLockRepository productRepository;
  private final ProductServiceImpl productService;

  public void decrese(Long id, int quantity){
    try{
      Long available = productRepository.getLock(id.toString());
      productService.purchase(id, quantity);
    }finally {
      productRepository.releaseLock(id.toString());
    }
  }
}
