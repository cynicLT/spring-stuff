package org.cynic.spring_stuff.framework.hint;

import liquibase.changelog.ChangeLogHistoryServiceFactory;
import liquibase.command.CommandFactory;
import org.hibernate.query.CommonQueryContract;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.hql.spi.SqmQueryImplementor;
import org.hibernate.query.spi.DomainQueryExecutionContext;
import org.hibernate.query.sqm.internal.SqmInterpretationsKey.InterpretationsKeySource;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class NativeImageRuntimeHint implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints
            .reflection()
//      Spring
            .registerType(
                TypeReference.of("org.springframework.core.annotation.TypeMappedAnnotation[]"),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
            )

//      Liquibase
            .registerType(
                TypeReference.of(CommandFactory.class),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
            )
            .registerType(
                TypeReference.of(ChangeLogHistoryServiceFactory.class),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
            )

//            Hibernate
            .registerType(TypeReference.of("org.jboss.jandex.DotName"),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS
            )
            .registerType(TypeReference.of(org.hibernate.query.sqm.tree.domain.AbstractSqmFrom.class),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS
            );

        hints
            .proxies()

//      Hibernate
            .registerJdkProxy(
                SqmQueryImplementor.class,
                InterpretationsKeySource.class,
                DomainQueryExecutionContext.class,
                SelectionQuery.class,
                CommonQueryContract.class
            );

        hints
            .resources()
//      Liquibase
            .registerPattern("database/changelog.yaml")
            .registerPattern("database/changes/*")
            .registerPattern("database/changes/files/*")

//      Apache Tika
            .registerPattern("org/apache/tika/mime/tika-mimetypes.xml");
    }
}
