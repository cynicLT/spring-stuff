package org.cynic.spring_stuff.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "MANAGER")
@DynamicInsert
@DynamicUpdate
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manager")
    @SequenceGenerator(name = "manager", sequenceName = "MANAGER_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "COMMENT")
    private String comment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "ID")
    private Organization organization;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private Set<Order> managingOrders;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Order> owningOrders;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private Set<Notification> notifications;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getComment() {
        return comment;
    }

    public Set<Order> getManagingOrders() {
        return managingOrders;
    }

    public Set<Order> getOwningOrders() {
        return owningOrders;
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }
}
