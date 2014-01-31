package com.rhcloud.application.vehtrack.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * Fleet entity is used to group users to devices and/or another subfleets
 */
@Data
@Entity
@Table(name = "fleets")
public class Fleet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fleet_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "fleets")
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "fleets")
    private Set<Device> devices = new HashSet<>();

    @OneToMany(orphanRemoval = true)
    Set<Fleet> subFeets;
}
