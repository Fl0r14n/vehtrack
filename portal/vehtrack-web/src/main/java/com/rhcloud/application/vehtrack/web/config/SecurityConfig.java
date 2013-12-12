package com.rhcloud.application.vehtrack.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan("com.rhcloud.application.vehtrack.web.security")
@ImportResource({ "classpath*:security.xml" })
public class SecurityConfig {
    
}
