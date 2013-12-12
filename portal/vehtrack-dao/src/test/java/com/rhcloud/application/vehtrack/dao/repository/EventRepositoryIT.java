package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Device;
import com.rhcloud.application.vehtrack.domain.Event;
import java.util.Date;
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
public class EventRepositoryIT {

    @Autowired
    EventRepository eventRepo;
    @Autowired
    DeviceRepository deviceRepo;

    @Before
    public void setUp() {
        Device device = new Device();
        {
            device.setSerial("foo");
            device.setEmail("bar");
        }
        deviceRepo.save(device);
    }

    @After
    public void tearDown() {        
        eventRepo.deleteAll();
        deviceRepo.deleteAll();
    }

    @Test
    public void save_shoud_persist_an_event() {
        Device device = deviceRepo.findOne(1L);
        Event expected = new Event();
        {
            expected.setRecordedTimestamp(new Date());
            expected.setType(1);
            expected.setMessage("Hello world");
            expected.setDevice(device);
        }
        eventRepo.save(expected);
        Event result = eventRepo.findOne(1L);
        assertEquals(expected.getRecordedTimestamp(), result.getRecordedTimestamp());
    }
}
