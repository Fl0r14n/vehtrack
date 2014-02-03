package com.rhcloud.application.vehtrack.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 * This entity is a point with an attached timestamp. It does not have a coresponding table.
 */
@Data
@Embeddable
@MappedSuperclass
public class TimestampPoint extends Point {
    
    @Column(name = "recorded_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp; //ms
}