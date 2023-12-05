package org.cynic.spring_stuff.controller;

import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.model.DocumentModel;
import org.cynic.spring_stuff.service.DocumentService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class DocumentControllerTest {

    private DocumentController documentController;
    @Mock
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        this.documentController = new DocumentController(documentService);
    }

    @Test
    void itemWhenOK() {
        Long id = Instancio.create(Long.class);
        DocumentModel documentModel = Instancio.create(DocumentModel.class);

        Mockito.when(documentService.getDocumentById(id)).thenReturn(documentModel);

        Assertions.assertThat(documentController.item(id))
            .matches(it -> HttpStatus.OK.isSameCodeAs(it.getStatusCode()))
            .matches(it -> documentModel.fileName().equals(it.getHeaders().getContentDisposition().getFilename()))
            .matches(it -> documentModel.mimeType().equals(it.getHeaders().getContentDisposition().getType()))
            .extracting(HttpEntity::getBody)
            .isEqualTo(new ByteArrayResource(documentModel.content()));

    }

    @Test
    void deleteWhenOK() {
        Long id = Instancio.create(Long.class);

        documentController.delete(id);

        Mockito.verify(documentService, Mockito.times(1)).deleteBy(id);
    }
}