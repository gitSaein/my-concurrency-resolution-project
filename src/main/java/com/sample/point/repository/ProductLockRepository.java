package com.sample.point.repository;

import com.sample.point.domains.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductLockRepository extends JpaRepository<Products, Long> {

  @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
  Long getLock(@Param("key") String key);

  @Query(value = "select release_lock(:key)", nativeQuery = true)
  void releaseLock(@Param("key") String key);
}
