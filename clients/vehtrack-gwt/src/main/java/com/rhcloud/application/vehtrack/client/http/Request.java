package com.rhcloud.application.vehtrack.client.http;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Request {

    public enum HttpMethod {

        DELETE, GET, HEAD, POST, PUT;
    }
    private Authentification authentification;
    private HttpMethod httpMethod = HttpMethod.GET;
    private Map<String, String> headers;
    private final String url;
    private String text = "";

    public Request(String url) {
        this.url = url;
    }
}
