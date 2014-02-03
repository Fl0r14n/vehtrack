package com.rhcloud.application.vehtrack;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.rhcloud.application.vehtrack.domain.Point;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.Data;

public class YourNavigationOrg {

    /**
     * It uses http://www.yournavigation.org/ to generate a journey
     *
     * @param start
     * @param stop
     * @return
     * @throws IOException
     */
    public KML getKML(Point start, Point stop) throws IOException {
        return execute(KML.class, buildURL(start, stop));
    }

    //##########################################################################
    private <T> T execute(Class<T> cls, String urlPath) throws IOException {
        URL url = new URL(urlPath);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        if (HttpURLConnection.HTTP_OK != request.getResponseCode()) { //we do GET so we should get only 200 if all ok
            StringBuilder buf = new StringBuilder();
            InputStream is = request.getErrorStream();
            int ch;
            while ((ch = is.read()) != -1) {
                buf.append((char) ch);
            }
            throw new IOException(buf.toString());
        }
        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(request.getInputStream(), cls);
    }

    private String buildURL(Point start, Point stop) {
        StringBuilder buf = new StringBuilder();
        buf.append("http://www.yournavigation.org/api/dev/route.php?flat=");
        buf.append(start.getLatitude()).append("&flon=").append(stop.getLongitude()).append("&tlat=").append(stop.getLatitude()).append("&tlon=").append(stop.getLongitude());
        buf.append("&v=motorcar&fast=1&layer=mapnik&instructions=0");
        return buf.toString();
    }

    @Data
    @JacksonXmlRootElement(localName = "kml")
    public static class KML {

        @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
        private String xmlns;
        @JacksonXmlProperty(localName = "Document")
        private Document document;

        @Data
        public static class Document {

            @JacksonXmlProperty(localName = "name")
            private String name;
            @JacksonXmlProperty(localName = "open")
            private Integer open;
            @JacksonXmlProperty(localName = "distance")
            private Double distance;
            @JacksonXmlProperty(localName = "traveltime")
            private Long traveltime;
            @JacksonXmlProperty(localName = "description")
            private String description;
            @JacksonXmlProperty(localName = "Folder")
            private Folder folder;

            @Data
            public static class Folder {

                @JacksonXmlProperty(localName = "name")
                private String name;
                @JacksonXmlProperty(localName = "visibility")
                private Integer visibility;
                @JacksonXmlProperty(localName = "description")
                private String decription;
                @JacksonXmlProperty(localName = "Placemark")
                private Placemark placemark;

                @Data
                public static class Placemark {

                    @JacksonXmlProperty(localName = "name")
                    private String name;
                    @JacksonXmlProperty(localName = "visibility")
                    private Integer visibility;
                    @JacksonXmlProperty(localName = "description")
                    private String description;
                    @JacksonXmlProperty(localName = "LineString")
                    private LineString lineString;

                    @Data
                    public static class LineString {

                        @JacksonXmlProperty(localName = "tessellate")
                        private Integer tessellate;
                        @JacksonXmlProperty(localName = "coordinates")
                        private String coordinates;
                    }
                }
            }
        }
    }
}
