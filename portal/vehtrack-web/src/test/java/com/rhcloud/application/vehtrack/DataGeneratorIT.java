package com.rhcloud.application.vehtrack;

import com.rhcloud.application.vehtrack.dao.repository.AccountRepository;
import com.rhcloud.application.vehtrack.dao.repository.DeviceRepository;
import com.rhcloud.application.vehtrack.dao.repository.EventRepository;
import com.rhcloud.application.vehtrack.dao.repository.FleetRepository;
import com.rhcloud.application.vehtrack.dao.repository.JourneyRepository;
import com.rhcloud.application.vehtrack.dao.repository.PositionRepository;
import com.rhcloud.application.vehtrack.dao.repository.UserRepository;
import com.rhcloud.application.vehtrack.domain.Device;
import com.rhcloud.application.vehtrack.domain.Fleet;
import com.rhcloud.application.vehtrack.domain.Journey;
import com.rhcloud.application.vehtrack.domain.Position;
import com.rhcloud.application.vehtrack.domain.User;
import com.rhcloud.application.vehtrack.web.config.ApplicationConfig;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
public class DataGeneratorIT {
    
    private static String ADMIN_USER = "admin";
    private static String ADMIN_PASSWORD = "hackme";
    
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private FleetRepository fleetRepository;
    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UserRepository userRepository;
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void generateData() {        
        generateAdminUser();
        List<User> users = generateUsers();
        
        List<Device> devices = generateDevices();
        for(Device device: devices) {
            List<Journey> journeys = generateJourneysForDevice(device);
            for(Journey journey: journeys) {
                generatePositions(device, journey);
                generateEvents(device, journey);
                generateEvents(device, journey);
            }
        }
    }    
    
    public void generateAdminUser() {
        
    }
    
    public List<User> generateUsers() {        
        return null;
    }
    
    public List<Device> generateDevices() {
        return null;
    }
    
    public List<Journey> generateJourneysForDevice(Device device) {
        return null;
    }
    
    public void generatePositions(Device device, Journey journey) {
        
    }
    
    public void generateEvents(Device device, Journey journey) {
        
    }
    
    public Fleet generateFleet(List<User> owners, boolean hasSubFleet) {
        return null;
    }
}
