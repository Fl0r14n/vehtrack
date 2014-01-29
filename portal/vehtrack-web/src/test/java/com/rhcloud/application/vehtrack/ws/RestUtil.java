package com.rhcloud.application.vehtrack.ws;

import com.jayway.restassured.path.json.JsonPath;
import com.rhcloud.application.vehtrack.ws.resources.LinkResource;
import com.rhcloud.application.vehtrack.ws.resources.LinkedResource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class RestUtil {

    public static < T> String toJson(final T resource) throws IOException {
        return new ObjectMapper().writeValueAsString(resource);
    }

    public static < T> T fromJson(final String json, final Class< T> clazzOfResource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        {
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper.readValue(json, clazzOfResource);
    }

    public static String getEntityID(JsonPath jsonPath) throws NumberFormatException, IOException {
        List<Map<String, String>> links = jsonPath.getList("content.links");
        for (Map<String, String> link : links) {
            if (link.get("rel").equals("self")) {
                String str = link.get("href");
                return str.substring(str.lastIndexOf('/') + 1);
            }
        }
        throw new IOException("Could not locate element id");
    }

    public static LinkResource addLink(LinkedResource resource, String relation) {
        String path = "";
        for (LinkResource link : resource.getLinks()) {
            if (link.getRel().equals("self")) {
                path = link.getHref();
            }
        }
        LinkResource deviceLink = new LinkResource();
        {
            deviceLink.setRel(relation);
            deviceLink.setHref(path);
        }
        return deviceLink;
    }
}
