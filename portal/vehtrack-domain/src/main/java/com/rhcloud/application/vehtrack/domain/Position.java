package com.rhcloud.application.vehtrack.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Position is an entity sent by device that describes the device status at a sampled time.
 */
@Data
@Entity
@Table(name = "positions")
public class Position extends TPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    @ManyToOne
    @JoinColumn(name = "journey_id", nullable = true)
    private Journey journey;
    
    @Column(name = "speed")
    private BigDecimal speed; //km/h
}
