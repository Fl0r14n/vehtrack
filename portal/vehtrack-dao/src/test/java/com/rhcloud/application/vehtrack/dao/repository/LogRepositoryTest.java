package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.Device;
import com.rhcloud.application.vehtrack.domain.LEVEL;
import com.rhcloud.application.vehtrack.domain.Log;
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
public class LogRepositoryTest {

    @Autowired
    private LogRepository eventRepo;
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
        }
        device = deviceRepo.save(device);
        Log expected = new Log();
        {
            expected.setTimestamp(new Date());
            expected.setType(LEVEL.INFO);
            expected.setMessage("Hello world");
            expected.setDevice(device);
        }
        eventRepo.save(expected);
        Log actual = eventRepo.findOne(1L);
        assertEquals(expected.getTimestamp(), actual.getTimestamp());
    }
}
