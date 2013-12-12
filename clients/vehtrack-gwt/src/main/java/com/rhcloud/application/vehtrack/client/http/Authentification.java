package com.rhcloud.application.vehtrack.client.http;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Authentification {

    public enum AuthentificationType {

        BROWSER,
        BASIC,
        DIGEST_MD5;
    }
    private AuthentificationType authentificationType;
    private String username;
    private String password;

    public Authentification() {
        authentificationType = AuthentificationType.BROWSER;
    }

    public Authentification(AuthentificationType authentificationType, String username, String password) {
        this.authentificationType = authentificationType;
        this.username = username;
        this.password = password;
    }
}
