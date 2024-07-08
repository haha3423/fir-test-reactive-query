package org.example.fir.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.querydsl.core.Tuple;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.example.fir.service.DynamicQueryService1.JoinInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@ComponentScan("org.example")
public class DynamicQueryServiceTest {

  @Autowired
  private EntityManager entityManager;

  DynamicQueryService1 dynamicQueryService1;
  DynamicQueryService2 dynamicQueryService2;

  @BeforeEach
  public void setUp() {
    dynamicQueryService1 = new DynamicQueryService1(entityManager);
    dynamicQueryService2 = new DynamicQueryService2(entityManager);
  }

  @Test
  @DisplayName("TEST 01-1 : Entity와 map객체로 진행하는 동적 쿼리")
  @Sql("/test-data.sql")
  @Transactional
  public void testExecuteDynamicQueryWithEntityName() throws ClassNotFoundException {

    String tableName = "TestTotalCOA";

    List<Map<String, String>> selectColumns = List.of(
        Map.of("table", "TestTotalCOA",
            "column", "accountName",
            "compute", "none",
            "alias", "accountName"),
        Map.of("table", "TestTransaction",
            "column", "transactionAmount",
            "compute", "none",
            "alias", "amount")
//        Map.of("table", "TestTransaction",
//            "column", "transactionAmount",
//            "compute", "AVG",
//            "alias", "average")
    );

    List<JoinInfoDto> joins = List.of(
        new JoinInfoDto("TestTotalCOA", "accountId", "TestTransaction", "accountId", "left")
    );

    List<Map<String, String>> whereConditions = List.of(
        Map.of("table", "TestTotalCOA",
            "column", "groupName",
            "value", "Finance")
    );

    List<Map<String, String>> orderByColumns = List.of(
        Map.of("column", "accountName",
            "order", "asc")
    );

//    List<Map<String, String>> groupByColumns = List.of(
//        Map.of("table", "TestTotalCOA", "column", "accountName")
//    );

    List<Tuple> results = dynamicQueryService1.executeDynamicQueryWithEntityName(
        tableName, selectColumns, joins, whereConditions, orderByColumns, null
    );

    assertThat(results).isNotEmpty();
    System.out.println("---------------------------------------------------- ");
    System.out.println("---- results: " + results);
    for (Tuple result : results) {
      // @todo 추후 인덱스 외 Expression을 통해 컬럼 명으로 접근 가능한지 확인 필요
      String accountName = result.get(0, String.class);
      Double amount = result.get(1, Double.class);
      System.out.println(
          "---- AccountName: " + accountName
              + "\n---- Amount: " + amount);
    }
  }

  @Test
  @DisplayName("TEST 01-2 : Entity와 map객체로 진행하는 동적 쿼리 + SUM, AVG 등 함수 및 그룹화 추가")
  @Sql("/test-data.sql")
  @Transactional
  public void testExecuteDynamicQueryWithEntityName_WithComputing() throws ClassNotFoundException {

    String tableName = "TestTotalCOA";

    List<Map<String, String>> selectColumns = List.of(
        Map.of("table", "TestTotalCOA",
            "column", "accountName",
            "compute", "none",
            "alias", "accountName"),
        Map.of("table", "TestTransaction",
            "column", "transactionAmount",
            "compute", "SUM",
            "alias", "sumTransactionAmount"),
        Map.of("table", "TestTransaction",
            "column", "transactionAmount",
            "compute", "AVG",
            "alias", "avgTransactionAmount")
    );

    List<JoinInfoDto> joins = List.of(
        new JoinInfoDto("TestTotalCOA", "accountId", "TestTransaction", "accountId", "LEFT")
    );

    List<Map<String, String>> whereConditions = List.of(
        Map.of("table", "TestTotalCOA",
            "column", "groupName",
            "value", "Finance")
    );

    List<Map<String, String>> orderByColumns = List.of(
        Map.of("column", "sumTransactionAmount",
            "order", "desc")
    );

    List<Map<String, String>> groupByColumns = List.of(
        Map.of("table", "TestTotalCOA",
            "column", "accountName")
    );

    List<Tuple> results = dynamicQueryService1.executeDynamicQueryWithEntityName(
        tableName, selectColumns, joins, whereConditions, orderByColumns, groupByColumns
    );

    assertThat(results).isNotEmpty();
    System.out.println("---------------------------------------------------- ");
    System.out.println("---- results: " + results);
    for (Tuple result : results) {
      String accountName = result.get(0, String.class);
      Double sumTransactionAmount = result.get(1, Double.class);
      Double avgTransactionAmount = result.get(2, Double.class);
      System.out.println(
          "---- AccountName: " + accountName
              + "\n---- SumTransactionAmount: " + sumTransactionAmount
              + "\n---- AvgTransactionAmount: " + avgTransactionAmount);
    }
  }


  @Test
  @DisplayName("TEST 02 : EntityManager.creativeNativeQuery를 이용한 동적 쿼리 (유사 MyBatis)")
  @Sql("/test-data.sql")
  @Transactional
  public void testExecuteDynamicQueryWithTableName() {
    List<String> tableNames = List.of("test_total_coa");
    List<String> selectColumns = List.of(
        "test_total_coa.account_name",
        "SUM(test_transaction.transaction_amount)",
        "AVG(test_transaction.transaction_amount)"
    );

    List<DynamicQueryService2.JoinInfoDto> joins = new ArrayList<>();
    joins.add(
        new DynamicQueryService2.JoinInfoDto("test_total_coa", "account_id", "test_transaction",
            "account_id", "LEFT"));

    List<String> whereConditions = List.of("test_total_coa.group_name=Finance");
    List<String> orderByColumns = List.of("SUM(test_transaction.transaction_amount) desc");
    List<String> groupByColumns = List.of("test_total_coa.account_name");

    List<Object[]> results = dynamicQueryService2.executeDynamicQueryWithTableName(
        tableNames, selectColumns, joins, whereConditions, orderByColumns, groupByColumns);

    assertThat(results).isNotEmpty();
    System.out.println("---------------------------------------------------- ");
    System.out.println("---- results: " + results);
    for (Object[] result : results) {
      String accountName = (String) result[0];
      BigDecimal transactionAmount = (BigDecimal) result[1];
      System.out.println(
          "AccountName: " + accountName + ", TransactionAmount: " + transactionAmount);
    }
  }

}
