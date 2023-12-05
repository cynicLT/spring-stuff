package org.cynic.spring_stuff.mapper;

import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Organization;
import org.cynic.spring_stuff.domain.http.organization.OrganizationHttp;
import org.cynic.spring_stuff.domain.http.organization.OrganizationHttp.ManageOrganizationHttp;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({InstancioExtension.class})
@Tag("unit")
class OrganizationMapperTest {

    private OrganizationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrganizationMapperImpl();
    }

    @Test
    void toHttpWhenOK() {
        Manager manager = Instancio.create(Manager.class);

        Organization organization = Instancio.of(Organization.class)
            .set(Select.field("managers"), Set.of(manager))
            .create();

        OrganizationHttp expected = new OrganizationHttp(
            organization.getId(),
            organization.getName(),
            organization.getEmail(),
            organization.getPhone(),
            organization.getAddress(),
            organization.getComment(),
            Set.of(
                new ManageOrganizationHttp(
                    manager.getId(),
                    manager.getName(),
                    manager.getEmail(),
                    manager.getPhone(),
                    manager.getComment()
                ))
        );

        Assertions.<Optional<OrganizationHttp>>assertThat(mapper.toHttp(organization))
            .extracting(Optional::get)
            .isEqualTo(expected);

    }
}