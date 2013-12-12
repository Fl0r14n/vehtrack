package com.rhcloud.application.vehtrack.client.widgets;

import com.smartgwt.client.widgets.grid.ListGrid;

public class DevicesWidget extends ContentWidget {

    public DevicesWidget() {
        super("Devices");
        final ListGrid devicesGrid = new ListGrid();
        {
            
        }
        super.addSummary(devicesGrid);
        super.addDetails(null);
    }
}
