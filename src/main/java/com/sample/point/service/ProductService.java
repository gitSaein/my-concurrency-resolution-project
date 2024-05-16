package com.sample.point.service;

import com.sample.point.domains.Products;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {
  @Transactional
  void purchase(Long id, int quantity);
  Products getBy(Long id);
}
