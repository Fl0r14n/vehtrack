package com.rhcloud.application.vehtrack.client.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class LinkJSO extends JavaScriptObject {

    protected LinkJSO() {
    }

    public final native String getRel() /*-{
     return this.rel;
     }-*/;

    public final native void setRel(String rel) /*-{
     this.rel = rel;
     }-*/;

    public final native String getHref() /*-{
     return this.href;
     }-*/;

    public final native void setHref(String href) /*-{
     this.href = href;
     }-*/;
}
