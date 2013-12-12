package com.rhcloud.application.vehtrack.client.http;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpClient {

    private static final Logger l = Logger.getLogger(HttpClient.class.getName());
    
    public void send(Request request, final HttpCallback callback) {
        if (request == null || callback == null) {
            throw new NullPointerException();
        }
        l.log(Level.FINE, request.toString());
        XMLHttpRequest xmlHttpRequest = XMLHttpRequest.create();
        try {
            Authentification authentification = request.getAuthentification();
            if (authentification != null && authentification.getAuthentificationType().equals(Authentification.AuthentificationType.BASIC)) {
                xmlHttpRequest.open(request.getHttpMethod().name(), request.getUrl(), authentification.getUsername(), authentification.getPassword());
            } else {
                xmlHttpRequest.setWithCredentials(authentification!=null && Authentification.AuthentificationType.BROWSER.equals(authentification.getAuthentificationType()));
                xmlHttpRequest.open(request.getHttpMethod().name(), request.getUrl());
            }
            //MD5 AUTH outside browser not supported cuz of browser issue. Tested on Chrome and Firefox
            setHeaders(xmlHttpRequest, request.getHeaders());
            xmlHttpRequest.setOnReadyStateChange(new ReadyStateChangeHandler() {
                @Override
                public void onReadyStateChange(XMLHttpRequest xhr) {
                    if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                        xhr.clearOnReadyStateChange();
                        callback.handleResponse(buildResponse(xhr));
                    }
                }
            });
            xmlHttpRequest.send(request.getText());
        } catch (JavaScriptException jse) {
            l.log(Level.SEVERE, jse.getMessage(), jse);
        }
    }

    public interface HttpCallback {

        void handleResponse(Response response);
    }

    private Response buildResponse(XMLHttpRequest xhr) {
        Response response = new Response();
        {
            for(Response.Status status : Response.Status.values()) {
                if(status.getValue()==xhr.getStatus()) {
                    response.setStatus(status);
                    break;
                }
            }
            response.setText(xhr.getResponseText());
            response.setResponseType(Response.ResponseType.fromString(xhr.getResponseType()));
            response.setHeaders(getHeaders(xhr));
        }
        l.log(Level.FINE, response.toString());
        return response;
    }

    private void setHeaders(XMLHttpRequest xhr, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                xhr.setRequestHeader(header.getKey(), header.getValue());
            }
        } else {
            xhr.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
        }
    }

    private Map<String, String> getHeaders(XMLHttpRequest xhr) {
        String allHeaders = xhr.getAllResponseHeaders();
        String[] headers = allHeaders.split("\n");
        Map<String, String> result = new HashMap();
        for(String header: headers) {
            int separator = header.indexOf(':');
            result.put(header.substring(0, separator).trim(), header.substring(separator+1).trim());
        }
        return result;
    }
}
