package org.cynic.spring_stuff.mapper;

import org.cynic.spring_stuff.domain.entity.Organization;
import org.cynic.spring_stuff.domain.http.organization.OrganizationHttp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Optional;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class OrganizationMapper {

    protected abstract OrganizationHttp internal(Organization organization);

    public Optional<OrganizationHttp> toHttp(Organization organization) {
        return Optional.ofNullable(internal(organization));
    }
}
