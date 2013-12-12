package com.rhcloud.application.vehtrack.client.rest;

import com.google.gwt.core.client.JavaScriptObject;
import com.rhcloud.application.vehtrack.client.http.HttpClient;
import com.rhcloud.application.vehtrack.client.http.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestClient {

    private static final Logger l = Logger.getLogger(HttpClient.class.getName());
    
    public void request(JsonRequest request, final JsonCallback callback) {
        if (request != null) {
            Map<String, String> headers = new HashMap();
            {
                headers.put("Content-Type", "application/json");
            }
            request.setHeaders(headers);
            request.fromJson();
        }
        HttpClient hc = new HttpClient();
        hc.send(request, new HttpClient.HttpCallback() {
            @Override
            public void handleResponse(Response response) {
                String contentType = response.getHeaders().get("Content-Type");
                if (contentType != null && (contentType.equals("application/json") || contentType.equals("application/octet-stream"))) {
                    //octet-stream is hack for spring data rest on put methods
                    JsonResponse jsonr = new JsonResponse(response);
                    {
                        jsonr.toJson();
                    }
                    callback.handleJsonResponse(jsonr);
                } else {
                    l.log(Level.WARNING, "Content type is not application/json. Aborting");
                }
            }
        });
    }

    public interface JsonCallback<T extends JavaScriptObject> {

        void handleJsonResponse(JsonResponse<T> response);
    }
}
