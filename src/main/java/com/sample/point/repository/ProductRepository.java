package com.sample.point.repository;

import com.sample.point.domains.Products;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Products, Long> {

  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Products p where p.id=:id")
  Products findByIdWithPessimisticLock(@Param("id") Long id);
  @Lock(value = LockModeType.OPTIMISTIC)
  @Query("select p from Products p where p.id=:id")
  Products findByIdWithOptimisticLock(@Param("id") Long id);
}
