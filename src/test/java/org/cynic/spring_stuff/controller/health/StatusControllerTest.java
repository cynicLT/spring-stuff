package org.cynic.spring_stuff.controller.health;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class StatusControllerTest {

    private StatusController statusController;

    @BeforeEach
    void setUp() {
        this.statusController = new StatusController();
    }

    @Test
    void indexWhenOK() {
        Assertions.assertThat(statusController.index())
            .isEqualTo("OK");
    }
}