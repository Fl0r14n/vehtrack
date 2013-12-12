package com.rhcloud.application.vehtrack.client.jso;

import java.util.Date;

public class EventJSO extends LinksJSO {

    protected EventJSO() {
    }

    public final native Date getRecordedTimestamp() /*-{
     return this.recordedTimestamp;
     }-*/;

    public final native void setRecordedTimestamp(Date recordedTimestamp) /*-{
     this.recordedTimestamp = recordedTimestamp;
     }-*/;

    public final native Integer getType() /*-{
     return this.type;
     }-*/;

    public final native void setType(Integer type) /*-{
     this.type = type;
     }-*/;

    public final native String getMessage() /*-{
     return this.message;
     }-*/;

    public final native void setMessage(String message) /*-{
     this.message = message;
     }-*/;

    public final native LinkJSO getDevice() /*-{
     return this.device;
     }-*/;

    public final native void setDevice(LinkJSO device) /*-{
     this.device = device;
     }-*/;

    public final native LinkJSO getJourney() /*-{
     return this.journey;
     }-*/;

    public final native void setJourney(LinkJSO journey) /*-{
     this.journey = journey;
     }-*/;
}
