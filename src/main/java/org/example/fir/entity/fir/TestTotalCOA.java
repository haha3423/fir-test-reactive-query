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
@Table(name = "TEST_TOTAL_COA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TestTotalCOA {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "ACCOUNT_ID")
  private String accountId;

  @Column(name = "ACCOUNT_NAME")
  private String accountName;

  @Column(name = "GROUP_ID")
  private String groupId;

  @Column(name = "GROUP_NAME")
  private String groupName;

  @Column(name = "USER_ID")
  private Long userId;

}
