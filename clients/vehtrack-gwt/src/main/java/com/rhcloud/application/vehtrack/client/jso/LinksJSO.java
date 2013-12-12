package com.rhcloud.application.vehtrack.client.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class LinksJSO extends JavaScriptObject {
    
    protected LinksJSO() {
    }

    public final native JsArray<LinkJSO> getLinks() /*-{
     return this.links;
     }-*/;

    public final native void setLinks(JsArray<LinkJSO> links) /*-{
     this.links = links;
     }-*/;
}
