package org.cynic.spring_stuff.mapper;

import java.util.Optional;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.http.manager.ManagerHttp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class ManagerMapper {


    protected abstract ManagerHttp internal(Manager manager);

    public Optional<ManagerHttp> toHttp(Manager manager) {
        return Optional.ofNullable(internal(manager));
    }

}
