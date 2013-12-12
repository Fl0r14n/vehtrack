package com.rhcloud.application.vehtrack.client.datasource;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

public interface DataSourceConvertor<T extends JavaScriptObject> {

    /**
     * Called when DataSource is initalized. Used to set columns and column types
     */
    void onInit(DataSource ds);

    
    Record[] toRecord(T jso);
    
    
    String toID(Record oldRecord);
}
