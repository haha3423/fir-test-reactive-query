package org.example.fir.entity.fir;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTestUser is a Querydsl query type for TestUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestUser extends EntityPathBase<TestUser> {

    private static final long serialVersionUID = 1049006810L;

    public static final QTestUser testUser = new QTestUser("testUser");

    public final StringPath email = createString("email");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userName = createString("userName");

    public QTestUser(String variable) {
        super(TestUser.class, forVariable(variable));
    }

    public QTestUser(Path<? extends TestUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestUser(PathMetadata metadata) {
        super(TestUser.class, metadata);
    }

}

