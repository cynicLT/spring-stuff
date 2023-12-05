package org.cynic.spring_stuff.domain.model;

import java.io.Serial;
import java.io.Serializable;

public record DocumentModel(Long id, String fileName, String mimeType, byte[] content) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
