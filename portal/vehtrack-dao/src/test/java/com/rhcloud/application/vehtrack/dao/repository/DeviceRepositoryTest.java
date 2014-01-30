package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Device;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:repository-context.xml")
public class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Before
    public void setUp() {
        device = new Device();
        {
            
        }
    }
    private Device device;

    @After
    public void tearDown() {

    }
    
    @Test
    public void save_should_persist_a_device() {
        
    }
}
