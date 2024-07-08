package org.example.fir.repository.queryDsl;


import com.querydsl.jpa.impl.JPAQueryFactory;

public class DynamicQueryRepository {

  private final JPAQueryFactory queryFactory; //ApplicationConfiguration 에 Bean 생성

  public DynamicQueryRepository(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

//    public List<Map<String, Object>> search() {
//        List<Tuple> dynamicList = queryFactory
//                .select()
//                .from(AccountLedger)
//                .where()
//                .fetch()
//
//    }

}
