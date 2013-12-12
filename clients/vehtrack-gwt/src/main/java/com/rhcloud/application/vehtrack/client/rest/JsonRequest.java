package com.rhcloud.application.vehtrack.client.rest;

import com.google.gwt.core.client.JavaScriptObject;
import com.rhcloud.application.vehtrack.client.http.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class JsonRequest extends Request {

    private JavaScriptObject json;

    public JsonRequest(String url) {
        super(url);
    }

    protected void fromJson() {
        if (json != null) {
            setText(Marshaller.marshall(json));
        }
    }
}
