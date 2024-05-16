package com.sample.point;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sample.point.domains.Products;
import com.sample.point.repository.ProductRepository;
import com.sample.point.service.ProductService;
import com.sample.point.service.impl.NamedLockProductFacade;
import com.sample.point.service.impl.ProductOptimisticLockServiceImpl;
import com.sample.point.service.impl.ProductPessimisticLockServiceImpl;
import com.sample.point.service.impl.ProductServiceImpl;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
class PointApplicationTests {
  @Autowired
  ProductServiceImpl productService;
  @Autowired
  ProductOptimisticLockServiceImpl productOptimisticLockService;
  @Autowired
  ProductPessimisticLockServiceImpl productPessimisticLockService;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  NamedLockProductFacade namedLockProductFacade;

  @Test
  @DisplayName("동시에 100개 요청")
  void contextLoads() throws InterruptedException {
    //given
    int threadCount = 100;

    ExecutorService executorService = Executors.newFixedThreadPool(32);

    CountDownLatch latch = new CountDownLatch(threadCount);

    AtomicInteger k = new AtomicInteger();
    //when
    for(int i = 0; i < threadCount; i++){
      executorService.submit(() -> {
        try{
          System.out.println("######### " + k.getAndIncrement());
          productService.purchase(1L,1);
        }
        finally {
          latch.countDown();
        }
      });

    }
    latch.await();

    //then
    Products products = productService.getBy(1L);
    assertThat(products.getStock()).isEqualTo(0);
  }

  @Test
  @DisplayName("Named Lock 동시에 100개 요청")
  void NamedLock() throws InterruptedException {
    //given
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    //when
    for(int i = 0; i < threadCount; i++){
      executorService.submit(() -> {
        try{
          namedLockProductFacade.decrese(1L,1);
        }
        finally {
          latch.countDown();
        }
      });

    }
    latch.await();

    //then
    Products products = productService.getBy(1L);
    assertThat(products.getStock()).isEqualTo(0);
  }


  @Test
  @DisplayName("Pessimistic Write Lock 동시에 100개 요청")
  void PessimisticWriteLock() throws InterruptedException {
    //given
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    //when
    for(int i = 0; i < threadCount; i++){
      executorService.submit(() -> {
        try{
          productPessimisticLockService.purchase(1L,1);
        }
        finally {
          latch.countDown();
        }
      });

    }
    latch.await();

    //then
    Products products = productPessimisticLockService.getBy(1L);
    assertThat(products.getStock()).isEqualTo(0);
  }

  @Test
  @DisplayName("Optimistic Lock 동시에 100개 요청, 결과적으로는 충돌")
  void OptimisticLock() throws InterruptedException {
    //given
    int threadCount = 4;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger();

    //when
    for(int i = 0; i < threadCount; i++){
      executorService.submit(() -> {
        try{
          productOptimisticLockService.purchase(1L,1);
          successCount.getAndIncrement();
          System.out.println("성공");
        } catch (ObjectOptimisticLockingFailureException | InvalidDataAccessApiUsageException e){
          System.out.println("재시도");
          productOptimisticLockService.purchase(1L,1);
        } catch (Exception e){
          System.out.println(e.getMessage());
        }
        finally {
          latch.countDown();
        }
      });

    }
    latch.await();

    //then
    Products products = productOptimisticLockService.getBy(1L);
    assertThat(products.getStock()).isEqualTo(0);
  }
}
