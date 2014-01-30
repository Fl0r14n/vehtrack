package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.Device;
import com.rhcloud.application.vehtrack.domain.Event;
import com.rhcloud.application.vehtrack.domain.Fleet;
import com.rhcloud.application.vehtrack.domain.ROLE;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:repository-context.xml")
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private DeviceRepository deviceRepo;
    @Autowired
    private AccountRepository accountRepo;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        eventRepo.deleteAll();
        deviceRepo.deleteAll();
    }

    @Test
    public void save_shoud_persist_an_event() {
        Account account = new Account();
        {
            account.setLogin("foo");
            account.setPassword("bar");
            account.setRoles(new HashSet<>(Arrays.asList(ROLE.ADMIN)));
        }
        account = accountRepo.save(account); //must save everything. no cascadeing save
        Device device = new Device();
        {
            device.setAccount(account);
            device.setSerial("foo");
            device.setEmail("bar");
            device.setFleets(new HashSet<Fleet>());
        }
        device = deviceRepo.save(device);
        Event expected = new Event();
        {
            expected.setRecordedTimestamp(new Date());
            expected.setType(1);
            expected.setMessage("Hello world");
            expected.setDevice(device);
        }
        eventRepo.save(expected);
        Event actual = eventRepo.findOne(1L);
        //assertEquals(expected, actual); it calls equals and fetches lazy and if nothing is there we have a problem
        assertEquals(expected.getRecordedTimestamp(), actual.getRecordedTimestamp());
    }
}
