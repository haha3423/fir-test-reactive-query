package org.example.fir.entity.fir;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TEST_USER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TestUser {

  @Id
  @Column(name = "USER_ID")
  private Long userId;

  @Column(name = "USER_NAME")
  private String userName;

  @Column(name = "EMAIL")
  private String email;
}