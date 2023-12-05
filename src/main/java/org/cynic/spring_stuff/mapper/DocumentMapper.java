package org.cynic.spring_stuff.mapper;

import java.util.Optional;
import org.cynic.spring_stuff.domain.entity.Document;
import org.cynic.spring_stuff.domain.model.DocumentModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class DocumentMapper {


    protected abstract DocumentModel internal(Document document, String mimeType);

    public Optional<DocumentModel> toDocumentDto(Document document, String mimeType) {
        return Optional.ofNullable(internal(document, mimeType));
    }

}
