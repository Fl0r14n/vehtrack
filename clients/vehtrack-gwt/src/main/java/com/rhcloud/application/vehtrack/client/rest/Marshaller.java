package com.rhcloud.application.vehtrack.client.rest;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class Marshaller {
    
    public static <T extends JavaScriptObject> String marshall(T obj) {
        return new JSONObject(obj).toString();
    }
    
    public static native <T extends JavaScriptObject> T unmarshall(String json) /*-{
        return eval('(' + json + ')');
    }-*/;
}
