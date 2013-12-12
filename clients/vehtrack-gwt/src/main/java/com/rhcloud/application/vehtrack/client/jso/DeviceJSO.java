package com.rhcloud.application.vehtrack.client.jso;

import com.google.gwt.core.client.JsArray;

public class DeviceJSO extends LinksJSO {

    protected DeviceJSO() {
    }

    public final native String getSerial() /*-{
     return this.serial;
     }-*/;

    public final native void setSerial(String serial) /*-{
     this.serial = serial;
     }-*/;

    public final native String getEmail() /*-{
     return this.email;
     }-*/;

    public final native void setEmail(String email) /*-{
     this.email = email;
     }-*/;

    public final native JsArray<LinkJSO> getJourneys() /*-{
     return this.journeys;
     }-*/;

    public final native void setJourneys(JsArray<LinkJSO> journeys) /*-{
     this.journeys = journeys;
     }-*/;

    public final native JsArray<LinkJSO> getEvents() /*-{
     return this.events;
     }-*/;

    public final native void setEvents(JsArray<LinkJSO> events) /*-{
     this.events = events;
     }-*/;

    public final native LinkJSO getAccount() /*-{
     return this.account;
     }-*/;

    public final native void setAccount(LinkJSO account) /*-{
     this.account = account;
     }-*/;
}
