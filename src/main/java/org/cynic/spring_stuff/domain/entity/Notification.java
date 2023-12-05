package org.cynic.spring_stuff.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.type.YesNoConverter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "NOTIFICATION")
@DynamicInsert
@DynamicUpdate
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification")
    @SequenceGenerator(name = "notification", sequenceName = "NOTIFICATION_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATE_TIME")
    private OffsetDateTime dateTime;

    @Column
    @Convert(converter = YesNoConverter.class)
    private Boolean visit;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ORDER_PRICE_ID", referencedColumnName = "ID")
    private ItemOrderPrice itemOrderPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID", referencedColumnName = "ID")
    private Manager manager;

    public Long getId() {
        return id;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ItemOrderPrice getItemOrderPrice() {
        return itemOrderPrice;
    }

    public void setItemOrderPrice(ItemOrderPrice itemOrderPrice) {
        this.itemOrderPrice = itemOrderPrice;
    }

    public void setVisit(Boolean visit) {
        this.visit = visit;
    }

    public Boolean getVisit() {
        return visit;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }


}
