package com.rhcloud.application.vehtrack.ws.resources;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceResource extends LinkedResource {

    private String serial;
    private String email;
    private List<LinkResource> journeys;
    private List<LinkResource> events;
    private LinkResource account;
}
