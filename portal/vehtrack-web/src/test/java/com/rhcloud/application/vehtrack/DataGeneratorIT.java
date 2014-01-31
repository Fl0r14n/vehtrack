package com.rhcloud.application.vehtrack;

import com.rhcloud.application.vehtrack.dao.repository.AccountRepository;
import com.rhcloud.application.vehtrack.dao.repository.DeviceRepository;
import com.rhcloud.application.vehtrack.dao.repository.EventRepository;
import com.rhcloud.application.vehtrack.dao.repository.FleetRepository;
import com.rhcloud.application.vehtrack.dao.repository.JourneyRepository;
import com.rhcloud.application.vehtrack.dao.repository.PositionRepository;
import com.rhcloud.application.vehtrack.dao.repository.UserRepository;
import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.Device;
import com.rhcloud.application.vehtrack.domain.Event;
import com.rhcloud.application.vehtrack.domain.Fleet;
import com.rhcloud.application.vehtrack.domain.Journey;
import com.rhcloud.application.vehtrack.domain.Position;
import com.rhcloud.application.vehtrack.domain.ROLE;
import com.rhcloud.application.vehtrack.domain.User;
import com.rhcloud.application.vehtrack.web.config.ApplicationConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
public class DataGeneratorIT {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "hackme";
    private static final int TOTAL_USERS = 10;
    private static final int TOTAL_DEVICES = 100;
    private static final int MAX_JOURNEY_DEVICE = 50;
    private static final int MAX_POSITIONS_JOURNEY = 100;
    private static final int MAX_EVENTS_JOURNEY = 10;

    private static final Random random = new Random();
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
        Iterable<User> users = generateUsers();

        Iterable<Device> devices = generateDevices();
        for (Device device : devices) {
            Iterable<Journey> journeys = generateJourneysForDevice(device);
            for (Journey journey : journeys) {
                generatePositions(device, journey);
                generateEvents(device, journey);
                generateEvents(device, journey);
            }
        }
    }

    public void generateAdminUser() {
        Account account = new Account();
        {
            account.setLogin(ADMIN_USER);
            account.setPassword(ADMIN_PASSWORD);
            account.setRoles(new HashSet<>(Arrays.asList(ROLE.ADMIN)));
        }
        account = accountRepository.save(account);
        User user = new User();
        {
            user.setAccount(account);
            user.setEmail("admin@vehtrack.com");
            user.setName("Admin");
        }
        user = userRepository.save(user);
        Assert.assertNotNull(user);
    }

    public Iterable<User> generateUsers() {
        List<User> users = new ArrayList<>(TOTAL_USERS);
        for (int i = 0; i < TOTAL_USERS; i++) {
            User user = new User();
            {
                Account account = new Account();
                {
                    account.setLogin("user_"+i);
                    account.setPassword("pass_"+i);                    
                    account.setRoles(new HashSet<>(Arrays.asList(generateUserRole(i))));
                }
                account = accountRepository.save(account);
                user.setAccount(account);
                user.setEmail("user_"+i+"@somewhere.com");
                user.setName("User_"+i);
                users.add(user);
            }
            users.add(user);
        }
        return userRepository.save(users);
    }

    public Iterable<Device> generateDevices() {
        List<Device> devices = new ArrayList<>(TOTAL_DEVICES);
        //TODO
        return deviceRepository.save(devices);
    }

    public Iterable<Journey> generateJourneysForDevice(Device device) {
        List<Journey> journeys = new ArrayList<>(random.nextInt(MAX_JOURNEY_DEVICE));
        //TODO
        return journeyRepository.save(journeys);
    }

    public Iterable<Position> generatePositions(Device device, Journey journey) {
        List<Position> positions = new ArrayList<>(random.nextInt(MAX_POSITIONS_JOURNEY));
        //TODO
        return positionRepository.save(positions);
    }

    public Iterable<Event> generateEvents(Device device, Journey journey) {
        List<Event> events = new ArrayList<>(random.nextInt(MAX_EVENTS_JOURNEY));
        //TODO
        return eventRepository.save(events);
    }

    public Fleet generateFleet(List<User> owners, boolean hasSubFleet) {
        return null;
    }
    
    //##########################################################################
    private ROLE generateUserRole(int i) {
        ROLE[] roles = ROLE.values();
        return roles[i % roles.length];
    }
    
}
