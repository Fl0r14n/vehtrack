package com.rhcloud.application.vehtrack.client;

import com.google.gwt.core.client.EntryPoint;
import com.rhcloud.application.vehtrack.client.widgets.AccountWidget;
import com.rhcloud.application.vehtrack.client.widgets.RootWidget;

public class VehtrackGWT implements EntryPoint {

    @Override
    public void onModuleLoad() {
        RootWidget rootWidget = new RootWidget();
        rootWidget.addContentWidget(new AccountWidget());
        
        rootWidget.draw();
    }
}
