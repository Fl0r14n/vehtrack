package com.rhcloud.application.vehtrack.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * A device is an entity who sends information.
 */
@Data
@Entity
@Table(name = "devices")
public class Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "serial", nullable = false)
    private String serial;
    
    @Column(name = "type")
    private String type; //equipment type
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description; //VIN
    
    @Column(name = "email", nullable = false)
    private String email; //imei@server
    
    @Column(name = "plate")
    private String plate; //plate number
    
    @Column(name = "vin")
    private String vin; //Vehicle Identification Number
    
    @Column(name = "phone")
    private String phone; //phone number
    
    @Column(name = "imsi")
    private String imsi; //international mobile subscriber identity
    
    @Column(name = "msisdn")
    private String msisdn; //mobile subscriber integrated services digital network-number
    
    @OneToMany(orphanRemoval = true)
    private List<Journey> journeys;
    
    @OneToMany(orphanRemoval = true)
    private List<Event> events;
    
    @OneToOne(orphanRemoval = true, optional = false)
    @JoinColumn(name = "account_id", unique = true, nullable = false, updatable = false)
    private Account account;
    
    @ManyToMany(mappedBy = "devices")
    private List<Fleet> fleets;
}
