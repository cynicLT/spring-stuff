package org.cynic.spring_stuff.domain.entity;

import jakarta.persistence.*;
import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Set;

@Entity
@Table(name = "PRICE")
@DynamicInsert
@DynamicUpdate
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price")
    @SequenceGenerator(name = "price", sequenceName = "PRICE_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "\"VALUE\"")
    private BigDecimal value;

    @Column(name = "CURRENCY")
    private Currency currency;

    @Column(name = "DATE_TIME")
    private OffsetDateTime dateTime;

    @Column(name = "DUE_DATE_TIME")
    private OffsetDateTime dueDateTime;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private PriceType type;

    @Column(name = "COVERED")
    @Convert(converter = YesNoConverter.class)
    private Boolean covered;

    @OneToMany(mappedBy = "price", fetch = FetchType.LAZY)
    private Set<ItemOrderPrice> itemOrderPrices;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PRICE_DOCUMENT",
            joinColumns = @JoinColumn(name = "PRICE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "ID")
    )
    private Set<Document> documents;

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public PriceType getType() {
        return type;
    }

    public Boolean getCovered() {
        return covered;
    }

    public Long getId() {
        return id;
    }

    public Set<ItemOrderPrice> getItemOrderPrices() {
        return itemOrderPrices;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public OffsetDateTime getDueDateTime() {
        return dueDateTime;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDueDateTime(OffsetDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public void setType(PriceType type) {
        this.type = type;
    }

    public void setCovered(Boolean covered) {
        this.covered = covered;
    }
}
