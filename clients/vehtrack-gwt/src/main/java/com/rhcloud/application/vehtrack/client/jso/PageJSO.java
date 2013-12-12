package com.rhcloud.application.vehtrack.client.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class PageJSO extends JavaScriptObject {

    protected PageJSO() {
    }

    public final native Integer getSize() /*-{
     return this.size;
     }-*/;

    public final native void setSize(Integer size) /*-{
     this.size = size;
     }-*/;

    public final native Integer getTotalElements() /*-{
     return this.totalElements;
     }-*/;

    public final native void setTotalElements(Integer totalElements) /*-{
     this.totalElements = totalElements;
     }-*/;

    public final native Integer getTotalPages() /*-{
     return this.totalPages;
     }-*/;

    public final native void setTotalPages(Integer totalPages) /*-{
     this.totalPages = totalPages;
     }-*/;

    public final native Integer getNumber() /*-{
     return this.number;
     }-*/;

    public final native void setNumber(Integer number) /*-{
     this.number = number;
     }-*/;
}
