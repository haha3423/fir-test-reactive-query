package org.example.fir.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DynamicQueryService2 {

  @PersistenceContext
  EntityManager entityManager;

  public DynamicQueryService2(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Setter
  @Getter
  @Builder
  public static class JoinInfoDto {

    private String mainTable;
    private String mainColumn;
    private String joinTable;
    private String joinColumn;
    private String joinType; // { "LEFT", "RIGHT", "LEFT OUTER", "RIGHT OUTER" }
  }

  /**
   * <pre>
   * queryDsl을 이용한 다이나믹 쿼리 실행 메서드
   * SQL을 실행하는 방식으로 실행됨 >> MyBatis 유사
   * </pre>
   *
   * @version 1.0
   * @author jhcho
   * @Project FIR AUTOMATION
   * @Copyright Copyright (C) 2024 by MetLife All right reserved.
   *
   * <pre>
   * ============================================================
   * Date         Editor        수정내용
   * ------------------------------------------------------------
   * 2024-07-07   jhcho   최초 생성
   * </pre>
   */
  public List<Object[]> executeDynamicQueryWithTableName(
      List<String> tableNames,
      List<String> selectColumns,
      List<JoinInfoDto> joins,
      List<String> whereConditions,
      List<String> orderByColumns,
      List<String> groupByColumns) {

    // 동적으로 SQL 쿼리 생성
    StringBuilder sql = new StringBuilder("SELECT ");

    /*sql.append(selectColumns.stream()
          .map(column -> {
            // 집계 함수가 포함된 경우 그대로 사용, 그렇지 않은 경우 GROUP BY 대상
            if (column.matches(".*\\(.*\\)")) {
              return column;
            } else {
              return column;
            }
          })
          .collect(Collectors.joining(", ")));*/
    sql.append(String.join(", ", selectColumns));

    sql.append(" FROM ");
    sql.append(tableNames.get(0));

    // 동적으로 JOIN 절 추가
    if (joins != null) {
      for (JoinInfoDto join : joins) {
        sql.append(" ").append(join.getJoinType()).append(" JOIN ").append(join.getJoinTable())
            .append(" ON ").append(join.getMainTable()).append(".").append(join.getMainColumn())
            .append(" = ").append(join.getJoinTable()).append(".").append(join.getJoinColumn());
      }
    }


    // 동적으로 WHERE 절 추가
    if (whereConditions != null && !whereConditions.isEmpty()) {
      sql.append(" WHERE ");
      sql.append(whereConditions.stream()
          .map(condition -> {
            String[] parts = condition.split("=");
            return parts[0] + " = '" + parts[1] + "'";
          })
          // @todo OR 절에 대한 구분 필요
          .collect(Collectors.joining(" AND ")));
    }

    // 동적으로 GROUP BY 절 추가
    if (!groupByColumns.isEmpty()) {
      sql.append(" GROUP BY ");
      sql.append(String.join(", ", groupByColumns));

      // 그룹화되지 않은 선택된 모든 컬럼을 GROUP BY 절에 추가
      for (String selectColumn : selectColumns) {
        if (!groupByColumns.contains(selectColumn) && !selectColumn.matches(".*\\(.*\\)")) {
          sql.append(", ").append(selectColumn);
        }
      }
    }

    // 동적으로 ORDER BY 절 추가
    if (orderByColumns != null && !orderByColumns.isEmpty()) {
      sql.append(" ORDER BY ");
      sql.append(orderByColumns.stream()
          .map(column -> {
            String[] parts = column.split(" ");
            // @todo ASC 외 DESC에 대한 구분 필요
            return parts[0] + (parts.length > 1 ? " " + parts[1] : " ASC");
          })
          .collect(Collectors.joining(", ")));
    }

    // 쿼리 실행
    try {
      Query query = entityManager.createNativeQuery(sql.toString());
      return query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

}