package org.cynic.spring_stuff.domain.entity;


import jakarta.persistence.*;
import org.cynic.spring_stuff.domain.model.type.RateSourceType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;

@Entity
@Table(name = "RATE")
@DynamicInsert
@DynamicUpdate
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate")
    @SequenceGenerator(name = "rate", sequenceName = "RATE_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATE_TIME")
    private OffsetDateTime dateTime;

    @Column(name = "CURRENCY")
    private Currency currency;

    @Column(name = "SOURCE")
    @Enumerated(EnumType.STRING)
    private RateSourceType source;

    @Column(name = "\"VALUE\"")
    private BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency code) {
        this.currency = code;
    }

    public RateSourceType getSource() {
        return source;
    }

    public void setSource(RateSourceType source) {
        this.source = source;
    }


}
