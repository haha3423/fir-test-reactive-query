package org.example.fir.entity.fir;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTestTransaction is a Querydsl query type for TestTransaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestTransaction extends EntityPathBase<TestTransaction> {

    private static final long serialVersionUID = 1424551151L;

    public static final QTestTransaction testTransaction = new QTestTransaction("testTransaction");

    public final StringPath accountId = createString("accountId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> transactionAmount = createNumber("transactionAmount", Double.class);

    public final StringPath transactionDate = createString("transactionDate");

    public QTestTransaction(String variable) {
        super(TestTransaction.class, forVariable(variable));
    }

    public QTestTransaction(Path<? extends TestTransaction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestTransaction(PathMetadata metadata) {
        super(TestTransaction.class, metadata);
    }

}

