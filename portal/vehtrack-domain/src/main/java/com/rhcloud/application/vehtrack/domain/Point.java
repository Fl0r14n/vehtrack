package com.rhcloud.application.vehtrack.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 * This entity describes a minimal gps position. Does not have a coresponding table
 */
@Data
@Embeddable
@MappedSuperclass
public class Point implements Serializable {

    @Column(name = "latitude")
    private BigDecimal latitude; //gg.ggggg
    
    @Column(name = "longitude")
    private BigDecimal longitude; //gg.ggggg
}
