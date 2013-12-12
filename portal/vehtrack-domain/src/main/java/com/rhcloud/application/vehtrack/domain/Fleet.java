package com.rhcloud.application.vehtrack.domain;

import java.util.List;
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
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @ManyToMany
    private List<User> users;
    
    @ManyToMany
    private List<Device> devices;
    
    @OneToMany
    List<Fleet> subFeets;
}
