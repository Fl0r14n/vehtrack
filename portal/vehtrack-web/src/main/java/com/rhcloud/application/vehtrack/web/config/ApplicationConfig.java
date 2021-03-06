package com.rhcloud.application.vehtrack.web.config;

import com.rhcloud.application.vehtrack.dao.config.DBConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.rhcloud.application.vehtrack"})
@Import({DBConfig.class,SecurityConfig.class})
public class ApplicationConfig {    
}
