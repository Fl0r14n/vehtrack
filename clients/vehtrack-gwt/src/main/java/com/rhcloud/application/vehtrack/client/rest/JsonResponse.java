package com.rhcloud.application.vehtrack.client.rest;

import com.google.gwt.core.client.JavaScriptObject;
import com.rhcloud.application.vehtrack.client.http.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class JsonResponse<T extends JavaScriptObject> extends Response {
    
    public JsonResponse() {
    }

    public JsonResponse(Response response) {
        //copy constructor
        super.setHeaders(response.getHeaders());
        super.setResponseType(response.getResponseType());
        super.setStatus(response.getStatus());
        super.setText(response.getText());
    }
    
    private T json;
    
    protected void toJson() {
        String jsonAsString = getText();
        if(jsonAsString == null || "".equals(jsonAsString)) {
            jsonAsString = "{}";
        }
        json = Marshaller.unmarshall(jsonAsString);
    }
}
