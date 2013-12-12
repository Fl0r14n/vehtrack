package com.rhcloud.application.vehtrack.ws.resources;

import lombok.Data;

@Data
public class PageResource {

    private int size;
    private int totalElements;
    private int totalPages;
    private int number;
}
