package org.cynic.spring_stuff.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "DOCUMENT")
@DynamicInsert
@DynamicUpdate
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document")
    @SequenceGenerator(name = "document", sequenceName = "DOCUMENT_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Lob
    @Column(name = "CONTENT")
    private byte[] content;

    @ManyToMany(mappedBy = "documents", fetch = FetchType.LAZY)
    private Set<Order> orders;

    @ManyToMany(mappedBy = "documents", fetch = FetchType.LAZY)
    private Set<Item> items;

    @ManyToMany(mappedBy = "documents", fetch = FetchType.LAZY)
    private Set<Price> prices;

    public byte[] getContent() {
        return content;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public Set<Item> getItems() {
        return items;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public Set<Price> getPrices() {
        return prices;
    }


}
