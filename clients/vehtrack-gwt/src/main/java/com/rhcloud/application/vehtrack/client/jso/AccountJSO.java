package com.rhcloud.application.vehtrack.client.jso;

import com.google.gwt.core.client.JsArray;

public class AccountJSO extends LinksJSO {

    protected AccountJSO() {
    }

    public final String getLogin() {
        String href = super.getLinks().get(0).getHref();        
        return href.substring(href.lastIndexOf('/')+1);
    }

    public final native void setLogin(String login) /*-{
     this.login = login;
     }-*/;

    public final native String getPassword() /*-{
     return this.password;
     }-*/;

    public final native void setPassword(String password) /*-{
     this.password = password;
     }-*/;

    public final native JsArray getRoles() /*-{
     return this.roles;
     }-*/;

    public final native void setRoles(JsArray roles) /*-{
     this.roles = roles;
     }-*/;

    public final native boolean getLocked() /*-{
     return this.locked;
     }-*/;

    public final native void setLocked(boolean locked) /*-{
     this.locked = locked;
     }-*/;
}
