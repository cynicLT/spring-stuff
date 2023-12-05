package org.cynic.spring_stuff.service;

import jakarta.persistence.LockModeType;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Document;
import org.cynic.spring_stuff.domain.model.DocumentModel;
import org.cynic.spring_stuff.mapper.DocumentMapper;
import org.cynic.spring_stuff.repository.DocumentRepository;
import org.cynic.spring_stuff.repository.ItemRepository;
import org.cynic.spring_stuff.repository.OrderRepository;
import org.cynic.spring_stuff.repository.PriceRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class DocumentService {

    private static final Metadata METADATA = new Metadata();

    private final DocumentRepository documentRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final PriceRepository priceRepository;
    private final DocumentMapper documentMapper;
    private final Detector detector;

    public DocumentService(DocumentRepository documentRepository,
        OrderRepository orderRepository,
        ItemRepository itemRepository,
        PriceRepository priceRepository,
        DocumentMapper documentMapper,
        Detector detector) {

        this.documentRepository = documentRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.priceRepository = priceRepository;
        this.documentMapper = documentMapper;
        this.detector = detector;
    }

    public DocumentModel getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new ApplicationException("error.document.not-found", id));

        try (InputStream inputStream = new ByteArrayInputStream(document.getContent())) {
            MediaType mediaType = detector.detect(inputStream, METADATA);

            return documentMapper.toDocumentDto(document, mediaType.toString())
                .orElseThrow(() -> new ApplicationException("error.document.read", id));
        } catch (IOException e) {
            throw new ApplicationException("error.document.mime-type", e, id);
        }
    }


    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public void deleteBy(Long id) {
        Document document = documentRepository.findOne(DocumentRepository.byIdFetchingRefs(id))
            .orElseThrow(() -> new ApplicationException("error.document.not-found", id));

        document.getOrders().forEach(it -> {
            it.getDocuments().remove(document);
            orderRepository.save(it);
        });

        document.getItems().forEach(it -> {
            it.getDocuments().remove(document);
            itemRepository.save(it);
        });

        document.getPrices().forEach(it -> {
            it.getDocuments().remove(document);
            priceRepository.save(it);
        });

        documentRepository.delete(document);
    }
}
