package org.cynic.spring_stuff.function;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ThrowingFunctionTest {

    @Test
    void withTryHandledWhenError() {
        ApplicationException exception = Instancio.create(ApplicationException.class);
        Object item = Instancio.create(Object.class);

        Assertions.assertThatThrownBy(() -> ThrowingFunction.withTry(
                o -> {
                    throw exception;
                },
                throwable -> exception).apply(item))
            .asInstanceOf(InstanceOfAssertFactories.type(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), exception.getCode()))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactlyElementsOf(Arrays.asList(exception.getValues()));
    }


    @Test
    void withTryHandledWhenOk() {
        ApplicationException exception = Instancio.create(ApplicationException.class);

        Assertions.assertThat(
            ThrowingFunction.withTry(o -> true, throwable -> exception)
                .apply(null)).isTrue();
    }

}