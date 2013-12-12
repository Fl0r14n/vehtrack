package com.rhcloud.application.vehtrack.ws.resources;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventResource extends LinkedResource {

    private Date recordedTimestamp;
    private Integer type;
    private String message;
    private LinkResource device;
    private LinkResource journey;
}
