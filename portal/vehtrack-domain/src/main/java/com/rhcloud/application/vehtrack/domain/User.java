package com.rhcloud.application.vehtrack.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * A user is the entity who uses the information
 * @author fchis
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @OneToOne(orphanRemoval = true, optional = false)
    @JoinColumn(name = "account_id", unique = true, nullable = false, updatable = false)
    private Account account;
    
    @ManyToMany(mappedBy = "users")
    private List<Fleet> fleets;
}
