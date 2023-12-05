package org.cynic.spring_stuff.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Document;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.model.DocumentModel;
import org.cynic.spring_stuff.mapper.DocumentMapper;
import org.cynic.spring_stuff.repository.DocumentRepository;
import org.cynic.spring_stuff.repository.ItemRepository;
import org.cynic.spring_stuff.repository.OrderRepository;
import org.cynic.spring_stuff.repository.PriceRepository;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith({
        MockitoExtension.class,
        InstancioExtension.class
})
@Tag("unit")
class DocumentServiceTest {

    private final Metadata METADATA = new Metadata();
    private DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private Detector detector;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        this.documentService = new DocumentService(documentRepository, orderRepository, itemRepository, priceRepository, documentMapper, detector);
    }

    @Test
    void getContentByIdWhenOK() throws IOException {
        Long id = Instancio.create(Long.class);
        Document document = Instancio.create(Document.class);
        MediaType mediaType = Instancio.create(MediaType.class);
        DocumentModel http = Instancio.create(DocumentModel.class);

        Mockito.when(documentRepository.findById(id)).thenReturn(Optional.of(document));
        Mockito.when(detector.detect(Mockito.any(), Mockito.eq(METADATA))).thenReturn(mediaType);
        Mockito.when(documentMapper.toDocumentDto(document, mediaType.toString()))
                .thenReturn(Optional.of(http));

        Assertions.assertThat(documentService.getDocumentById(id))
                .isEqualTo(http);

        Mockito.verify(documentRepository, Mockito.times(1)).findById(id);
        Mockito.verify(detector, Mockito.times(1)).detect(Mockito.any(), Mockito.eq(METADATA));
        Mockito.verify(documentMapper, Mockito.times(1)).toDocumentDto(document, mediaType.toString());
    }

    @Test
    void getContentByIdWhenErrorMapping() throws IOException {
        Long id = Instancio.create(Long.class);
        Document document = Instancio.create(Document.class);
        MediaType mediaType = Instancio.create(MediaType.class);

        Mockito.when(documentRepository.findById(id)).thenReturn(Optional.of(document));
        Mockito.when(detector.detect(Mockito.any(), Mockito.eq(METADATA))).thenReturn(mediaType);
        Mockito.when(documentMapper.toDocumentDto(document, mediaType.toString()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> documentService.getDocumentById(id))
                .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
                .matches(it -> StringUtils.equals(it.getCode(), "error.document.read"))
                .extracting(ApplicationException::getValues)
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .containsExactly(id);

        Mockito.verify(documentRepository, Mockito.times(1)).findById(id);
        Mockito.verify(detector, Mockito.times(1)).detect(Mockito.any(), Mockito.eq(METADATA));
        Mockito.verify(documentMapper, Mockito.times(1)).toDocumentDto(document, mediaType.toString());
    }

    @Test
    void getContentByIdWhenErrorMimeType() throws IOException {
        Long id = Instancio.create(Long.class);
        Document document = Instancio.create(Document.class);
        IOException exception = Instancio.create(IOException.class);

        Mockito.when(documentRepository.findById(id)).thenReturn(Optional.of(document));
        Mockito.when(detector.detect(Mockito.any(), Mockito.eq(METADATA))).thenThrow(exception);

        Assertions.assertThatThrownBy(() -> documentService.getDocumentById(id))
                .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
                .matches(it -> StringUtils.equals(it.getCode(), "error.document.mime-type"))
                .matches(it -> exception.equals(it.getCause()))
                .extracting(ApplicationException::getValues)
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .containsExactly(id);

        Mockito.verify(documentRepository, Mockito.times(1)).findById(id);
        Mockito.verify(detector, Mockito.times(1)).detect(Mockito.any(), Mockito.eq(METADATA));
    }

    @Test
    void getContentByIdWhenErrorNotFound() {
        Long id = Instancio.create(Long.class);

        Assertions.assertThatThrownBy(() -> documentService.getDocumentById(id))
                .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
                .matches(it -> StringUtils.equals(it.getCode(), "error.document.not-found"))
                .extracting(ApplicationException::getValues)
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .containsExactly(id);

        Mockito.verify(documentRepository, Mockito.times(1)).findById(id);
    }


    @Test
    void deleteByWhenOk() {
        Long id = Instancio.create(Long.class);
        Item item = Instancio.of(Item.class)
                .set(Select.field("documents"), new HashSet<Document>())
                .create();

        Order order = Instancio.of(Order.class)
                .set(Select.field("documents"), new HashSet<Document>())
                .create();

        Price price = Instancio.of(Price.class)
                .set(Select.field("documents"), new HashSet<Document>())
                .create();

        Document document = Instancio.of(Document.class)
                .set(Select.field("items"), Set.of(item))
                .set(Select.field("orders"), Set.of(order))
                .set(Select.field("prices"), Set.of(price))
                .create();

        item.getDocuments().add(document);
        order.getDocuments().add(document);
        price.getDocuments().add(document);

        Mockito.when(documentRepository.findOne(Mockito.any())).thenReturn(Optional.of(document));

        documentService.deleteBy(id);

        Mockito.verify(documentRepository, Mockito.times(1)).findOne(Mockito.any());

        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
        Mockito.verify(priceRepository, Mockito.times(1)).save(price);
        Mockito.verify(documentRepository, Mockito.times(1)).delete(document);
    }


    @Test
    void deleteByWhenOkEmpty() {
        Long id = Instancio.create(Long.class);

        Document document = Instancio.of(Document.class)
                .set(Select.field("items"), Set.of())
                .set(Select.field("orders"), Set.of())
                .set(Select.field("prices"), Set.of())
                .create();

        Mockito.when(documentRepository.findOne(Mockito.any())).thenReturn(Optional.of(document));

        documentService.deleteBy(id);

        Mockito.verify(documentRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(documentRepository, Mockito.times(1)).delete(document);
    }

    @Test
    void deleteByWhenError() {
        Long id = Instancio.create(Long.class);

        Mockito.when(documentRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> documentService.deleteBy(id))
                .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
                .matches(it -> StringUtils.equals(it.getCode(), "error.document.not-found"))
                .extracting(ApplicationException::getValues)
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .containsExactly(id);

        Mockito.verify(documentRepository, Mockito.times(1)).findOne(Mockito.any());

    }
}
