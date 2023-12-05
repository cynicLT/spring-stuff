package org.cynic.spring_stuff.controller;

import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.model.DocumentModel;
import org.cynic.spring_stuff.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/documents")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> item(@PathVariable Long id) {
        DocumentModel content = documentService.getDocumentById(id);

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder(content.mimeType())
                    .filename(content.fileName())
                    .build()
                    .toString()
            )
            .header(HttpHeaders.CONTENT_TYPE, content.mimeType())
            .body(new ByteArrayResource(content.content()));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        documentService.deleteBy(id);
    }

}
