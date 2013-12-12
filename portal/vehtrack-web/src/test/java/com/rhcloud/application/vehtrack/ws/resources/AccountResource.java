package com.rhcloud.application.vehtrack.ws.resources;

import com.rhcloud.application.vehtrack.domain.ROLE;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountResource extends LinkedResource {

    private String login;
    private String password;
    private List<ROLE> roles;
}
