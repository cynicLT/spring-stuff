package org.cynic.spring_stuff.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "ITEM")
@DynamicInsert
@DynamicUpdate
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item")
    @SequenceGenerator(name = "item", sequenceName = "ITEM_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
    private Set<Order> orders;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_DOCUMENT",
        joinColumns = @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "ID")
    )
    private Set<Document> documents;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private Set<ItemOrderPrice> itemOrderPrices;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ItemOrderPrice> getItemOrderPrices() {
        return itemOrderPrices;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
