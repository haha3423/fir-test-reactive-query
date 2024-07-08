package org.example.fir.entity.fir;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTestTotalCOA is a Querydsl query type for TestTotalCOA
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestTotalCOA extends EntityPathBase<TestTotalCOA> {

    private static final long serialVersionUID = 2119088928L;

    public static final QTestTotalCOA testTotalCOA = new QTestTotalCOA("testTotalCOA");

    public final StringPath accountId = createString("accountId");

    public final StringPath accountName = createString("accountName");

    public final StringPath groupId = createString("groupId");

    public final StringPath groupName = createString("groupName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTestTotalCOA(String variable) {
        super(TestTotalCOA.class, forVariable(variable));
    }

    public QTestTotalCOA(Path<? extends TestTotalCOA> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestTotalCOA(PathMetadata metadata) {
        super(TestTotalCOA.class, metadata);
    }

}

