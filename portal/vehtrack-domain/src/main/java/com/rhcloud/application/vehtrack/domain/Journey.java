package com.rhcloud.application.vehtrack.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * A journey entity describes device activity from start to stop.
 */
@Data
@Entity
@Table(name = "journeys")
public class Journey implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journey_id")
    private Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "timestamp", column = @Column(name = "start_tstamp")),
        @AttributeOverride(name = "latitude", column = @Column(name = "start_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "start_longitude"))
    })
    private TPoint startPoint;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "timestamp", column = @Column(name = "stop_tstamp")),
        @AttributeOverride(name = "latitude", column = @Column(name = "stop_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "stop_longitude"))
    })
    private TPoint stopPoint;

    @Column(name = "distance")
    private BigDecimal distance; //m

    @Column(name = "avg_speed")
    private BigDecimal avgSpeed; //km/h

    @Column(name = "max_speed")
    private BigDecimal maxSpeed; //km/h

    @Column(name = "duration")
    private Long duration; //ms

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @OneToMany(orphanRemoval = false) //allowed only when device is deleted
    private List<Position> positions;

    @OneToMany(orphanRemoval = false) //allowed only when device is deleted
    private List<Log> logs;
}
