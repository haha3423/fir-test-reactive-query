package org.example.fir.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DynamicQueryService1 {

  @PersistenceContext
  EntityManager entityManager;

  public DynamicQueryService1(EntityManager entityManager) {
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
   * 테이블 네임 및 컬럼은 엔티티를 기반으로 함
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
  public List<Tuple> executeDynamicQueryWithEntityName(
      String tableNames,
      List<Map<String, String>> selectColumns,
      List<JoinInfoDto> joins,
      List<Map<String, String>> whereConditions,
      List<Map<String, String>> orderByColumns,
      List<Map<String, String>> groupByColumns) throws ClassNotFoundException {

    JPAQuery<Tuple> query = new JPAQuery<>(entityManager);

    // 파라미터로 from 절 테이블 추가
    PathBuilder<?> mainEntityPath = getPathBuilder(tableNames);
    query.from(mainEntityPath);

    // Join 된 엔티티를 저장하기 위한 맵
    Map<String, PathBuilder<?>> entityPaths = new HashMap<>();
    entityPaths.put(tableNames, mainEntityPath);

    if (joins != null && !joins.isEmpty()) {
      // 동적으로 join 절 추가
      for (JoinInfoDto join : joins) {
        PathBuilder<?> joinEntityPath = getPathBuilder(join.getJoinTable());
        entityPaths.put(join.getJoinTable(), joinEntityPath);

        // Querying
        query.leftJoin(joinEntityPath)
            .on(mainEntityPath.get(join.getMainColumn())
                .eq(joinEntityPath.get(join.getJoinColumn()))
            );
      }
    }

    // Select 절 추가
    query.select(selectColumns.stream()
        .map(column -> {
          // @todo 추후 구조가 고정된다면 DTO Conversion 해도 됨
          String table = column.get("table");
          String col = column.get("column");
          String compute = column.get("compute");
          String alias = column.get("alias");

          PathBuilder<?> entityPath = entityPaths.get(table);

          if (compute.equalsIgnoreCase("none")) {
            // 함수식이 컬럼에 포함되지 않은 경우 (SUM, AVG etc)
            return entityPath.get(col).as(alias);
          } else {
            // 함수식이 포함된 경우
            String template = compute + "(" + entityPath.get(col).toString() + ")";
            return Expressions.stringTemplate(template).as(alias);
          }
        }).toArray(Expression<?>[]::new)
    );

    // Where 절 추가
    if (whereConditions != null && !whereConditions.isEmpty()) {
      BooleanExpression whereExpression = null; // queryDsl T/F 객체

      for (Map<String, String> condition : whereConditions) {
        // @todo 추후 구조가 고정된다면 DTO Conversion 해도 됨
        String table = condition.get("table");
        String column = condition.get("column");
        String value = condition.get("value");

        PathBuilder<?> entityPath = entityPaths.get(table);

        BooleanExpression expr = entityPath.get(column, String.class).eq(value);
        // @todo 현재는 and만, Or절 구분할 수 있도록 처리 필요
        whereExpression = (whereExpression == null) ? expr : whereExpression.and(expr);
      }
      // Querying
      query.where(whereExpression);
    }

    // Group by 절 추가
    if (groupByColumns != null && !groupByColumns.isEmpty()) {
      query.groupBy(groupByColumns.stream()
          .map(column -> {
            String table = column.get("table");
            String col = column.get("column");

            PathBuilder<?> entityPath = entityPaths.get(table);
            return entityPath.get(col);
          }).toArray(Expression<?>[]::new)
      );

      // GROUP BY 절에 포함되지 않은 컬럼을 포함시킴
      // @todo 개선 필요
      for (Map<String, String> selectColumn : selectColumns) {
        String compute = selectColumn.get("compute");
        if ("none".equalsIgnoreCase(compute)) {
          String table = selectColumn.get("table");
          String column = selectColumn.get("column");

          if (groupByColumns.stream()
              .noneMatch(groupBy ->
                  groupBy.get("table").equals(table) && groupBy.get("column")
                      .equals(column))
          ) {
            PathBuilder<?> entityPath = entityPaths.get(table);
            query.groupBy(entityPath.get(column));
          }
        }
      }
    }

    // Order by 절 추가
    if (orderByColumns != null && !orderByColumns.isEmpty()) {
      query.orderBy(orderByColumns.stream()
          .map(order -> {
            // @todo select의 alias를 참조하고 있어 엔티티 속성으로 바꿔줄 필요 있음 + Join시 테이블 지정 필요
            String column = order.get("column");
            boolean isAsc = !"desc".equalsIgnoreCase(order.get("order"));
            return new OrderSpecifier<>(isAsc ? Order.ASC : Order.DESC,
                Expressions.stringTemplate(column));
          }).toArray(OrderSpecifier<?>[]::new)
      );
    }

    // get Data
    return query.fetch();
  }

  /**
   * <pre>
   * [PRIVATE] 테이블네임(String)으로 엔티티 클래스 객체 탐색
   * 탐색 후 PathBuilder(queryDsl)로 리턴
   * </pre>
   *
   * @version 1.0
   * @author jhcho
   * @Project FIR AUTOMATION
   * @Copyright Copyright (C) 2024 by MetLife All right reserved.
   * <pre>
   *
   * ============================================================
   * Date         Editor        수정내용
   * ------------------------------------------------------------
   * 2024-07-08   jhcho   최초 생성
   * </pre>
   */
  private PathBuilder<?> getPathBuilder(String tableName) throws ClassNotFoundException {
    String entityClassName = getEntityClassName(tableName);
    Class<?> entityClass = Class.forName(entityClassName);
    return new PathBuilder<>(entityClass, tableName.toLowerCase());
  }

  /**
   * <pre>
   * [PRIVATE] Entity로 선언된 테이블 객체에서 해당 tableName에 해당하는 객체가 있는지 체크하여 클래스 네임 리턴
   * </pre>
   * @version 1.0
   * @author jhcho
   * @Project FIR AUTOMATION
   * @Copyright Copyright (C) 2024 by MetLife All right reserved.
   * <pre>
   * ============================================================
   * Date         Editor        수정내용
   * ------------------------------------------------------------
   * 2024-07-08   jhcho   최초 생성
   * </pre>
   */
  private String getEntityClassName(String tableName) {
    for (EntityType<?> entityType : entityManager.getMetamodel().getEntities()) {
      if (entityType.getName().equalsIgnoreCase(tableName)) {
        return entityType.getJavaType().getName();
      }
    }
    throw new IllegalArgumentException("No entity found for table name: " + tableName);
  }

}