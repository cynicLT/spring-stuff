package org.cynic.spring_stuff.arhitecture;

import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.cynic.spring_stuff.Constants;
import org.junit.jupiter.api.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;


@Tag("unit")
@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class, packagesOf = Constants.class)
public class ApplicationTest {

    @ArchTest
    public static final ArchRule ALL_CLASSES_NOT_USING_PAIR =
            ArchRuleDefinition.noClasses()
                    .should().accessClassesThat()
                    .resideInAPackage("org.apache.commons.lang3.tuple..");


    @ArchTest
    public static final ArchRule NO_QUERY_ANNOTATION = ArchRuleDefinition.noMethods()
            .that().areDeclaredInClassesThat()
            .areAnnotatedWith(Repository.class)
            .should()
            .beAnnotatedWith(Query.class)
            .because("JPA Specification must be used");

    @ArchTest
    public static final ArchRule ALL_CONTROLLERS_MUST_BE_IN_PACKAGE =
            ArchRuleDefinition.classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .because("API endpoints  must be [controller] package");


    @ArchTest
    public static final ArchRule ALL_SERVICES_MUST_NOT_BE_TRANSACTIONAL =
            ArchRuleDefinition.classes()
                    .that().areAnnotatedWith(Component.class).and().resideInAPackage("..service..")
                    .should().notBeAnnotatedWith(Transactional.class)
                    .because("Service components must not be transacted");

    @ArchTest
    public static final ArchRule CONSTANTS_ARE_FINAL =
            ArchRuleDefinition.theClass(Constants.class)
                    .should().haveOnlyFinalFields()
                    .andShould().bePublic()
                    .andShould().notBeAnnotatedWith(Component.class)
                    .andShould().notBeAnnotatedWith(Service.class)
                    .because("Constants are not Business Logic");

    @ArchTest
    public static final ArchRule CONSTANTS_HAS_PRIVATE_CONSTRUCTOR =
            ArchRuleDefinition.constructors()
                    .that().areDeclaredIn(Constants.class)
                    .should().haveModifier(JavaModifier.PRIVATE)
                    .because("Constants are singletons");

}
