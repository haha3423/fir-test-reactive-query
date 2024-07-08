package org.example.fir.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class JoinInfoDto {
  private String mainTable;
  private String mainColumn;
  private String joinTable;
  private String joinColumn;
  private String joinType; // { "LEFT", "RIGHT", "LEFT OUTER", "RIGHT OUTER" }
}
