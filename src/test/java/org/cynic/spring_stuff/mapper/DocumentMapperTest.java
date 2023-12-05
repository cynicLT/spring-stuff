package org.cynic.spring_stuff.mapper;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.entity.Document;
import org.cynic.spring_stuff.domain.model.DocumentModel;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class DocumentMapperTest {

    private DocumentMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new DocumentMapperImpl();
    }

    @Test
    void toContentDtoWhenOk() {
        Document document = Instancio.create(Document.class);
        String mimeType = Instancio.create(String.class);

        DocumentModel expected = new DocumentModel(
            document.getId(),
            document.getFileName(),
            mimeType,
            document.getContent()
        );
        Assertions.<Optional<DocumentModel>>assertThat(mapper.toDocumentDto(document, mimeType))
            .extracting(Optional::get)
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "id",
                "fileName",
                "mimeType",
                "content"
            ).isEqualTo(expected);
    }
}