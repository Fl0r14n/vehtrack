package com.rhcloud.application.vehtrack.client.http;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Response {
    
    @Getter
    @AllArgsConstructor
    public enum Status {
        OK(200),
        Created(201),
        NoContent(204),
        Unauthorized(401);
        private int value;
    }

    @Getter
    @AllArgsConstructor
    public enum ResponseType {        
        TEXT("text"),
        ARRAY_BUFFER("arraybuffer"),
        BLOB("blob"),
        DOCUMENT("document"),
        JSON("json");
        private String type;
        
        public static ResponseType fromString(String type) {
            type = type.toLowerCase();
            if("arraybuffer".equals(type)) return ARRAY_BUFFER;
            if("blob".equals(type)) return BLOB;
            if("document".equals(type)) return DOCUMENT;
            if("json".equals(type)) return JSON;
            return TEXT;
        }
    }
        
    private Status status;
    private ResponseType responseType;
    private Map<String, String> headers;
    private String text;    
}
