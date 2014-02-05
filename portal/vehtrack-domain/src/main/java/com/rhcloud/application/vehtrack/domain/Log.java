package com.rhcloud.application.vehtrack.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 * An event is some information sent by the device (mostly alerts).
 */
@Data
@Entity
@Table(name = "logs")
public class Log implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;
    
    @Column(name = "tstamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LEVEL type;
    
    @Column(name = "message")
    private String message;
    
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    @ManyToOne
    @JoinColumn(name = "journey_id", nullable = true)
    private Journey journey;
}
