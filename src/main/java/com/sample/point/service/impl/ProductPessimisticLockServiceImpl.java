package com.sample.point.service.impl;

import com.sample.point.domains.Products;
import com.sample.point.repository.ProductRepository;
import com.sample.point.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductPessimisticLockServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  /**
   * 상품 구매
   * @param id 상품 id
   * @param quantity 구매 수
   */
  @Override
  @Transactional
  public void purchase(Long id, int quantity) {
    Products product = productRepository.findByIdWithPessimisticLock(id);
    product.decrease(quantity);
    productRepository.saveAndFlush(product);
  }

  /**
   * @param id 상품 id
   * @return Products
   */
  @Override
  @Transactional
  public Products getBy(Long id) {
    return productRepository.findByIdWithPessimisticLock(id);
  }
}
