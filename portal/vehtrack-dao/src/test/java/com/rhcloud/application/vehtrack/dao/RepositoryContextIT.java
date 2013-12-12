package com.rhcloud.application.vehtrack.dao;

import com.rhcloud.application.vehtrack.dao.repository.AccountRepository;
import com.rhcloud.application.vehtrack.dao.repository.DeviceRepository;
import com.rhcloud.application.vehtrack.dao.repository.EventRepository;
import com.rhcloud.application.vehtrack.dao.repository.FleetRepository;
import com.rhcloud.application.vehtrack.dao.repository.JourneyRepository;
import com.rhcloud.application.vehtrack.dao.repository.PositionRepository;
import com.rhcloud.application.vehtrack.dao.repository.UserRepository;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RepositoryContextIT {

    @Test
    public void context_and_repositories_should_exist() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:repository-context.xml");
        assertThat(context, is(notNullValue()));
        assertThat(context.getBean(AccountRepository.class), is(notNullValue()));
        assertThat(context.getBean(DeviceRepository.class), is(notNullValue()));
        assertThat(context.getBean(EventRepository.class), is(notNullValue()));
        assertThat(context.getBean(FleetRepository.class), is(notNullValue()));
        assertThat(context.getBean(JourneyRepository.class), is(notNullValue()));
        assertThat(context.getBean(PositionRepository.class), is(notNullValue()));
        assertThat(context.getBean(UserRepository.class), is(notNullValue()));
    }
}
