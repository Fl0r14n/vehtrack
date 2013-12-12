package com.rhcloud.application.vehtrack.ws.resources;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeneralResource<T> extends LinkedResource {

    private List<T> content;
    private PageResource page;
}
