package com.sample.point.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Products {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private int stock;
  @Column(nullable = false)
  private double price;
  @Version
  private Long version;

  public void decrease(final int stock){
    if(this.stock - stock < 0){
      throw new RuntimeException("재고 부족");
    }
    this.stock -= stock;
  }
}
