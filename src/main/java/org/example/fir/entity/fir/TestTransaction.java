package org.example.fir.entity.fir;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TEST_TRANSACTION")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TestTransaction {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "ACCOUNT_ID")
  private String accountId;

  @Column(name = "TRANSACTION_AMOUNT")
  private Double transactionAmount;

  @Column(name = "TRANSACTION_DATE")
  private String transactionDate;
}
