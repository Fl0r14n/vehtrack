package com.rhcloud.application.vehtrack.client.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class GeneralJSO<T extends JavaScriptObject> extends LinksJSO {

    protected GeneralJSO() {
    }

    public final native JsArray<T> getContent() /*-{
     return this.content;
     }-*/;

    public final native void setContent(JsArray<T> content) /*-{
     this.content = content;
     }-*/;

    public final native PageJSO getPage() /*-{
     return this.page;
     }-*/;

    public final native void setPage(PageJSO page) /*-{
     this.page = page;
     }-*/;
}
