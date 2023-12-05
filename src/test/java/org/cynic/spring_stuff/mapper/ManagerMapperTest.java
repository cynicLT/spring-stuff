package org.cynic.spring_stuff.mapper;


import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.http.manager.ManagerHttp;
import org.cynic.spring_stuff.domain.http.manager.ManagerHttp.ManagerOrganizationHttp;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
    InstancioExtension.class
})
@Tag("unit")
class ManagerMapperTest {

    private ManagerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ManagerMapperImpl();
    }

    @Test
    void toHttpWhenOK() {
        Manager manager = Instancio.create(Manager.class);

        ManagerHttp expected = new ManagerHttp(
            manager.getId(),
            manager.getName(),
            manager.getPhone(),
            manager.getEmail(),
            new ManagerOrganizationHttp(
                manager.getOrganization().getName(),
                manager.getOrganization().getEmail(),
                manager.getOrganization().getPhone(),
                manager.getOrganization().getAddress()
            )
        );

        Assertions.<Optional<ManagerHttp>>assertThat(mapper.toHttp(manager))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }
}