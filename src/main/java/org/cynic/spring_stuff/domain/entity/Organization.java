package org.cynic.spring_stuff.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "ORGANIZATION")
@DynamicInsert
@DynamicUpdate
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization")
    @SequenceGenerator(name = "organization", sequenceName = "ORGANIZATION_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "COMMENT")
    private String comment;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private Set<Manager> managers;

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getComment() {
        return comment;
    }

    public Long getId() {
        return id;
    }

    public Set<Manager> getManagers() {
        return managers;
    }
}
