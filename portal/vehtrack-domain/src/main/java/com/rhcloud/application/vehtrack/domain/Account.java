package com.rhcloud.application.vehtrack.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * Basic authentification object. Each user and device have one.
 */
@Data
@Entity
@Table(name = "accounts")
public class Account implements Serializable {

    @Id
    @Column(name = "login", nullable = false)
    private String login;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "locked")
    private Boolean locked = false;
    
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ROLE.class)
    @Column(name = "roles", nullable = false)
    private List<ROLE> roles;
}
