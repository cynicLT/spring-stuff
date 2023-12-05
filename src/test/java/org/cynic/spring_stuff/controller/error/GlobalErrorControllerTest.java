package org.cynic.spring_stuff.controller.error;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.http.ErrorHttp;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class GlobalErrorControllerTest {

    private GlobalErrorController globalErrorController;


    @BeforeEach
    void setUp() {
        this.globalErrorController = new GlobalErrorController();
    }

    @Test
    void handleErrorWhenUnknownStatus() {
        Integer statusCode = Instancio.of(Integer.class)
            .generate(Select.root(), it -> it.ints().min(600))
            .create();
        String errorMessage = Instancio.create(String.class);

        Assertions.assertThat(globalErrorController.handleError(Optional.ofNullable(statusCode), errorMessage))
            .matches(it -> HttpStatus.INTERNAL_SERVER_ERROR.isSameCodeAs(it.getStatusCode()))
            .extracting(HttpEntity::getBody)
            .matches(it -> StringUtils.equals(it.code(), "error.global"))
            .extracting(ErrorHttp::values)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(errorMessage);


    }

    @Test
    void handleErrorWhenKnownStatus() {
        Integer statusCode = Instancio.of(Integer.class)
            .generate(Select.root(), it -> it.ints().min(200).max(208))
            .create();
        String errorMessage = Instancio.create(String.class);

        Assertions.assertThat(globalErrorController.handleError(Optional.ofNullable(statusCode), errorMessage))
            .matches(it -> HttpStatus.valueOf(statusCode).isSameCodeAs(it.getStatusCode()))
            .extracting(HttpEntity::getBody)
            .matches(it -> StringUtils.equals(it.code(), "error.global"))
            .extracting(ErrorHttp::values)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(errorMessage);
    }
}