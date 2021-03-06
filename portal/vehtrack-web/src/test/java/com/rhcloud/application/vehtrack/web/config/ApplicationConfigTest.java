package com.rhcloud.application.vehtrack.web.config;

import com.rhcloud.application.vehtrack.dao.repository.LogRepository;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ApplicationConfigTest {

    @Test
    public void bootstrapAppFromJavaConfig() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        assertThat(context, is(notNullValue()));
        assertThat(context.getBean(LogRepository.class), is(notNullValue()));
    }
}
