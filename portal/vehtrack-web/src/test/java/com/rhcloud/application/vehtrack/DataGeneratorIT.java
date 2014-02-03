package com.rhcloud.application.vehtrack;

import com.rhcloud.application.vehtrack.YourNavigationOrg.KML;
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
import com.rhcloud.application.vehtrack.domain.Point;
import com.rhcloud.application.vehtrack.domain.Position;
import com.rhcloud.application.vehtrack.domain.ROLE;
import com.rhcloud.application.vehtrack.domain.User;
import com.rhcloud.application.vehtrack.web.config.ApplicationConfig;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
public class DataGeneratorIT {

    private static final Logger L = LoggerFactory.getLogger(DataGeneratorIT.class);

    private static final boolean WRITE_TO_DATABASE = false;
    private static final boolean GENERATE_ADMIN = false;
    private static final boolean GENERATE_USERS = true;
    private static final boolean GENERATE_DEVICES = true;
    private static final boolean GENERATE_JOURNEYS = true;
    private static final boolean GENERATE_POSITIONS = true;
    private static final boolean GENERATE_EVENTS = true;
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "hackme";
    private static final int TOTAL_USERS = 10;
    private static final int TOTAL_DEVICES = 100;
    private static final int MAX_JOURNEY_DEVICE = 50;
    private static final int MAX_POSITIONS_JOURNEY = 100;
    private static final int MAX_EVENTS_JOURNEY = 10;
    private static final String START_DATE = "2014-02-01";
    private static final String STOP_DATE = "2014-02-28";

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

    public DataGeneratorIT() throws ParseException {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        random = new Random();
        startDate = dateFormat.parse(START_DATE);
        stopDate = dateFormat.parse(STOP_DATE);
        yourNavigationOrg = new YourNavigationOrg();
    }
    private final SimpleDateFormat dateFormat;
    private final Date startDate;
    private final Date stopDate;
    private final Random random;
    private final YourNavigationOrg yourNavigationOrg;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void generateData() {
        if (GENERATE_ADMIN) {
            generateAdminUser();
        }
        Iterable<User> users = null;
        if (GENERATE_USERS) {
            users = generateUsers();
        }
        Iterable<Device> devices = null;
        if (GENERATE_DEVICES) {
            devices = generateDevices();
        }
        if (devices == null) {
            devices = deviceRepository.findAll();
        }
        for (Device device : devices) {
            Iterable<Journey> journeys = null;
            if (GENERATE_JOURNEYS) {
                journeys = generateJourneysForDevice(device);
            }
            if (journeys != null) {
                for (Journey journey : journeys) {
                    if (GENERATE_POSITIONS) {
                        generatePositions(device, journey);
                    }
                    if (GENERATE_EVENTS) {
                        generateEvents(device, journey);
                    }
                }
            }
        }
    }

    public void generateAdminUser() {
        L.info("Generating admin user========================================");
        User user = new User();
        {
            Account account = new Account();
            {
                account.setLogin(ADMIN_USER);
                account.setPassword(ADMIN_PASSWORD);
                account.setRoles(new HashSet<>(Arrays.asList(ROLE.ADMIN)));
            }
            if (WRITE_TO_DATABASE) {
                account = accountRepository.save(account);
            }
            user.setAccount(account);
            user.setEmail("admin@vehtrack.com");
            user.setName("Admin");
        }
        L.debug(user.toString());
        if (WRITE_TO_DATABASE) {
            user = userRepository.save(user);
            Assert.assertNotNull(user);
        }
    }

    public Iterable<User> generateUsers() {
        L.info("Generating users=============================================");
        List<User> users = new ArrayList<>(TOTAL_USERS);
        for (int i = 0; i < TOTAL_USERS; i++) {
            User user = new User();
            {
                Account account = new Account();
                {
                    account.setLogin("user_" + i);
                    account.setPassword("pass_" + i);
                    account.setRoles(new HashSet<>(Arrays.asList(generateUserRole(i))));
                }
                if (WRITE_TO_DATABASE) {
                    account = accountRepository.save(account);
                }
                user.setAccount(account);
                user.setEmail("user_" + i + "@somewhere.com");
                user.setName("User_" + i);
                users.add(user);
            }
            L.debug(user.toString());
            users.add(user);
        }
        if (WRITE_TO_DATABASE) {
            return userRepository.save(users);
        } else {
            return users;
        }
    }

    public Iterable<Device> generateDevices() {
        L.info("Generating devices===========================================");
        List<Device> devices = new ArrayList<>(TOTAL_DEVICES);
        for (int i = 0; i < TOTAL_DEVICES; i++) {
            Device device = new Device();
            {
                Account account = new Account();
                {
                    account.setLogin("device_" + i);
                    account.setPassword("pass_" + i);
                    account.setRoles(new HashSet<>(Arrays.asList(ROLE.DEVICE)));
                }
                if (WRITE_TO_DATABASE) {
                    account = accountRepository.save(account);
                }
                device.setAccount(account);
                device.setSerial("serial_" + i);
                device.setType("mk_" + (i % 3));
                device.setName("model_" + device.getType());
                device.setDescription("This is a mock device");
                device.setEmail("device_" + i + "@vehtrack.com");

                device.setPlate("");
                device.setVin("");
                device.setPhone("");
                device.setImsi("");
                device.setMsisdn("");
            }
            L.debug(device.toString());
            devices.add(device);
        }
        if (WRITE_TO_DATABASE) {
            return deviceRepository.save(devices);
        } else {
            return devices;
        }
    }

    public Iterable<Journey> generateJourneysForDevice(Device device) {
        L.info("Generating journeys for device " + device.getSerial() + "====");
        int totalJourneys = random.nextInt(MAX_JOURNEY_DEVICE);
        List<Journey> journeys = new ArrayList<>(totalJourneys);
        for (int i = 0; i < totalJourneys; i++) {
            //TODO
            Journey journey = new Journey();
            {
                journey.setDevice(device);
                journey.setStartPoint(null);
                journey.setStopPoint(null);
                journey.setDistance(BigDecimal.ZERO);
                journey.setAvgSpeed(BigDecimal.ZERO);
                journey.setMaxSpeed(BigDecimal.ZERO);
            }
            L.debug(journey.toString());
            journeys.add(journey);
        }
        if (WRITE_TO_DATABASE) {
            return journeyRepository.save(journeys);
        } else {
            return journeys;
        }
    }

    public Iterable<Position> generatePositions(Device device, Journey journey) {
        L.info("Generating positions for device " + device.getSerial() + " and journey " + journey.getId() + "====");
        int totalPositions = random.nextInt(MAX_POSITIONS_JOURNEY);
        List<Position> positions = new ArrayList<>(totalPositions);
        for (int i = 0; i < totalPositions; i++) {
            //TODO
            Position position = new Position();
            {

            }
            L.debug(position.toString());
            positions.add(position);
        }
        if (WRITE_TO_DATABASE) {
            return positionRepository.save(positions);
        } else {
            return positions;
        }
    }

    public Iterable<Event> generateEvents(Device device, Journey journey) {
        L.info("Generating events for device " + device.getSerial() + " and journey " + journey.getId() + "====");
        int totalEvents = random.nextInt(MAX_EVENTS_JOURNEY);
        List<Event> events = new ArrayList<>(totalEvents);
        for (int i = 0; i < totalEvents; i++) {
            //TODO
            Event event = new Event();
            {

            }
            L.debug(event.toString());
            events.add(event);
        }
        if (WRITE_TO_DATABASE) {
            return eventRepository.save(events);
        } else {
            return events;
        }
    }

    public Fleet generateFleet(List<User> owners, boolean hasSubFleet) {
        L.info("Generating fleets============================================");
        return null;
    }

    //##########################################################################
    private ROLE generateUserRole(int i) {
        ROLE[] roles = {ROLE.FLEET_ADMIN, ROLE.USER};
        return roles[i % roles.length];
    }
    
    public Journey generateJourney(Device device, Date startDate, Point start, Point stop) throws IOException {
        KML kml = yourNavigationOrg.getKML(start, stop);

        Double distance = kml.getDocument().getDistance(); //km
        Long traveltime = kml.getDocument().getTraveltime(); //sec
        String[] stringPoints = kml.getDocument().getFolder().getPlacemark().getLineString().getCoordinates().split(" ");
        List<Position> positions = new ArrayList<>(stringPoints.length);
        for (String stringPoint : stringPoints) {
            String[] latLng = stringPoint.split(",");
            Position position = new Position();
            {
                position.setDevice(device);
                //TODO calculate here
                position.setTimestamp(startDate);
                position.setLongitude(BigDecimal.valueOf(Double.parseDouble(latLng[0])));
                position.setLatitude(BigDecimal.valueOf(Double.parseDouble(latLng[1])));
                position.setSpeed(null);
            }
            positions.add(position);
        }
        if (WRITE_TO_DATABASE) {
            positions = (List<Position>) positionRepository.save(positions);
        }
        Journey journey = new Journey();
        {
            journey.setDevice(device);
            journey.setDuration(traveltime * 1000); //ms
            journey.setDistance(BigDecimal.valueOf(distance * 1000)); //m
            journey.setAvgSpeed(BigDecimal.valueOf(distance / traveltime)); //km/h
            journey.setMaxSpeed(journey.getAvgSpeed().add(BigDecimal.valueOf(30))); //+30km/h

            journey.setStartPoint(positions.get(0));
            journey.setStopPoint(positions.get(positions.size() - 1));
            journey.setPositions(positions);
        }
        if (WRITE_TO_DATABASE) {
            journey = journeyRepository.save(journey);
        }
        return journey;
    }
}
