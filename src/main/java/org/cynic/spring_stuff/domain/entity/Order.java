package org.cynic.spring_stuff.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.type.YesNoConverter;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "\"ORDER\"")
@DynamicInsert
@DynamicUpdate
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order")
    @SequenceGenerator(name = "order", sequenceName = "ORDER_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATE_TIME")
    private OffsetDateTime dateTime;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column
    @Convert(converter = YesNoConverter.class)
    private Boolean closed;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")
    private Manager owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID", referencedColumnName = "ID")
    private Manager manager;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ORDER_ITEM",
        joinColumns = @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID")
    )
    private Set<Item> items;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private Set<ItemOrderPrice> itemOrderPrices;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ORDER_DOCUMENT",
        joinColumns = @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "ID")
    )
    private Set<Document> documents;


    public Long getId() {
        return id;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public Manager getOwner() {
        return owner;
    }

    public Manager getManager() {
        return manager;
    }

    public Set<Item> getItems() {
        return items;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public Set<ItemOrderPrice> getItemOrderPrices() {
        return itemOrderPrices;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwner(Manager owner) {
        this.owner = owner;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Boolean getClosed() {
        return closed;
    }


}
