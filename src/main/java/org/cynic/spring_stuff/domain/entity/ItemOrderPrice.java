package org.cynic.spring_stuff.domain.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Entity
@Table(name = "ITEM_ORDER_PRICE")
@DynamicInsert
@DynamicUpdate
public class ItemOrderPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_order_price")
    @SequenceGenerator(name = "item_order_price", sequenceName = "ITEM_ORDER_PRICE_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FRACTION")
    private BigDecimal fraction;

    @Column(name = "COMMENT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "PRICE_ID", referencedColumnName = "ID")
    private Price price;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "itemOrderPrice")
    private Notification notification;

    public BigDecimal getFraction() {
        return fraction;
    }

    public String getComment() {
        return comment;
    }

    public Item getItem() {
        return item;
    }

    public Order getOrder() {
        return order;
    }

    public Price getPrice() {
        return price;
    }

    public Long getId() {
        return id;
    }

    public Notification getNotification() {
        return notification;
    }


    public void setFraction(BigDecimal fraction) {
        this.fraction = fraction;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
