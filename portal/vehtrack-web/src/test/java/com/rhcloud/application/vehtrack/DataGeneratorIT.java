package com.rhcloud.application.vehtrack;

import com.rhcloud.application.vehtrack.YourNavigationOrg.KML;
import com.rhcloud.application.vehtrack.dao.repository.AccountRepository;
import com.rhcloud.application.vehtrack.dao.repository.DeviceRepository;
import com.rhcloud.application.vehtrack.dao.repository.LogRepository;
import com.rhcloud.application.vehtrack.dao.repository.FleetRepository;
import com.rhcloud.application.vehtrack.dao.repository.JourneyRepository;
import com.rhcloud.application.vehtrack.dao.repository.PositionRepository;
import com.rhcloud.application.vehtrack.dao.repository.UserRepository;
import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.Device;
import com.rhcloud.application.vehtrack.domain.Log;
import com.rhcloud.application.vehtrack.domain.Fleet;
import com.rhcloud.application.vehtrack.domain.Journey;
import com.rhcloud.application.vehtrack.domain.LEVEL;
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

    private static final boolean WRITE_TO_DATABASE = true;
    private static final boolean GENERATE_ADMIN = false;
    private static final boolean GENERATE_USERS = true;
    private static final boolean GENERATE_DEVICES = true;
    private static final boolean GENERATE_JOURNEYS = true;
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "hackme";
    private static final int TOTAL_USERS = 10;
    private static final int TOTAL_DEVICES = 100;
    private static final int MIN_POSITIONS_JOURNEY = 100;
    private static final int MAX_POSITIONS_JOURNEY = 150;
    private static final int MAX_LOGS_JOURNEY = 5;
    private static final String START_DATE = "2014-02-01";
    private static final String STOP_DATE = "2014-05-30";

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private LogRepository logRepository;
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
        cities = new ArrayList<>();
        {
            Point p;
            //Timisoara
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(45.760098));
                p.setLongitude(BigDecimal.valueOf(21.238579));
            }
            //Arad
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(46.166704));
                p.setLongitude(BigDecimal.valueOf(21.316663));
            }
            //Oradea
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(47.077137));
                p.setLongitude(BigDecimal.valueOf(21.921791));
            }
            //Cluj
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(46.78196));
                p.setLongitude(BigDecimal.valueOf(23.600639));
            }
            //Iasi
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(47.162641));
                p.setLongitude(BigDecimal.valueOf(27.589706));
            }
            //Brasov
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(45.660127));
                p.setLongitude(BigDecimal.valueOf(25.611137));
            }
            //Constanta
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(44.179496));
                p.setLongitude(BigDecimal.valueOf(28.63993));
            }
            //Bucuresti
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(44.427283));
                p.setLongitude(BigDecimal.valueOf(26.092773));
            }
            //Craiova
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(44.316234));
                p.setLongitude(BigDecimal.valueOf(23.801681));
            }
            //Sibiu
            cities.add(p = new Point());
            {
                p.setLatitude(BigDecimal.valueOf(45.791946));
                p.setLongitude(BigDecimal.valueOf(24.142059));
            }
        }
    }
    private final SimpleDateFormat dateFormat;
    private Date startDate;
    private final Date stopDate;
    private final Random random;
    private final YourNavigationOrg yourNavigationOrg;
    private final List<Point> cities;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void generateData() throws IOException {
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

    public Iterable<Journey> generateJourneysForDevice(Device device) throws IOException {
        L.info("Generating journeys for device " + device.getSerial() + "====");
        Point startPoint = cities.get(random.nextInt(cities.size()));
        List<Journey> journeys = new ArrayList<>();
        while (startDate.before(stopDate)) {
            Point endPoint;
            do {
                endPoint = cities.get(random.nextInt(cities.size()));
            } while (startPoint.getLatitude().equals(endPoint.getLatitude()));
            Journey journey = generateJourney(device, startDate, startPoint, endPoint);
            if (journey != null) {
                L.debug(journey.toString());
                journeys.add(journey);
                //reinit stuff
                startPoint = endPoint;
                startDate = new Date(startDate.getTime() + journey.getDuration() + 3600000);
            }
        }
        if (WRITE_TO_DATABASE) {
            return journeyRepository.save(journeys);
        } else {
            return journeys;
        }
    }

    public Fleet generateFleet(List<User> owners, boolean hasSubFleet) {
        L.info("Generating fleets============================================");
        //TODO
        return null;
    }

    //##########################################################################
    private ROLE generateUserRole(int i) {
        ROLE[] roles = {ROLE.FLEET_ADMIN, ROLE.USER};
        return roles[i % roles.length];
    }

    private Journey generateJourney(Device device, Date startDate, Point start, Point stop) throws IOException {
        KML kml = yourNavigationOrg.getKML(start, stop);
        if (kml == null) {
            return null;
        }
        //generate positions
        Double distance = kml.getDocument().getDistance(); //km
        Long traveltime = kml.getDocument().getTraveltime(); //sec

        List<Position> positions = getPositionsFromKML(kml);
        if (positions == null) {
            return null;
        }
        positions = trimPositions(positions);
        Long timeStep = traveltime / positions.size();
        Long timestamp = startDate.getTime();
        Point lastPoint = start;
        for (Position position : positions) {
            position.setDevice(device);
            position.setTimestamp(new Date(timestamp));
            //L.debug("Last point: " + lastPoint.getLatitude() + "," + lastPoint.getLongitude() + " Postition: " + position.getLatitude() + "," + position.getLongitude() + " Timestep: " + timeStep);
            BigDecimal dst = calculateDistance(lastPoint, position);
            //L.debug("Distance: " + dst + " km");
            BigDecimal speed = dst.multiply(BigDecimal.valueOf(3600/timeStep));
            //L.debug("Speed: " + speed + " km/h");            
            position.setSpeed(speed);
            //increment stuff
            timestamp += timeStep;
            lastPoint = position;
        }

        if (WRITE_TO_DATABASE) {
            positions = (List<Position>) positionRepository.save(positions);
        }
        //generate journey
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
            journey.setLogs((List<Log>) generateLogsForJourney(device));
        }
        if (WRITE_TO_DATABASE) {
            journey = journeyRepository.save(journey);
        }
        return journey;
    }

    private List<Position> getPositionsFromKML(KML kml) {
        String attribute = kml.getDocument().getFolder().getPlacemark().getLineString().getCoordinates();
        if (attribute == null || attribute.isEmpty() || attribute.equals("0,0")) {
            L.error("Empty points list in kml");
            return null;
        }
        String[] stringPoints = attribute.split("\n");
        List<Position> points = new ArrayList<>(stringPoints.length);
        for (String stringPoint : stringPoints) {
            String[] latLng = stringPoint.split(",");
            if (latLng.length == 2) {
                double latitude = Double.parseDouble(latLng[1]);
                double longitude = Double.parseDouble(latLng[0]);
                Position point = new Position();
                {
                    point.setLatitude(BigDecimal.valueOf(latitude));
                    point.setLongitude(BigDecimal.valueOf(longitude));
                }
                points.add(point);
            }
        }
        return points;
    }

    private List<Position> trimPositions(List<Position> positions) {
        int totalPositions = MIN_POSITIONS_JOURNEY + random.nextInt(MAX_POSITIONS_JOURNEY-MIN_POSITIONS_JOURNEY);
        if (positions.size() > totalPositions) {
            int removeStep = positions.size() / totalPositions;
            if (removeStep > 0) {
                List<Position> result = new ArrayList<>();                
                int i = 0;
                for (Position position : positions) {
                    if ((i % removeStep) == 0) {
                        result.add(position);
                    }
                    i++;
                }
                //L.debug("Initial: "+positions.size()+" After: "+result.size());
                return result;
            }
        }        
        return positions;
    }

    private BigDecimal calculateDistance(Point a, Point b) {
        BigDecimal latD2km = a.getLatitude().subtract(b.getLatitude()).multiply(BigDecimal.valueOf(111.12)).pow(2);
        BigDecimal lonD2km = a.getLongitude().subtract(b.getLongitude()).multiply(BigDecimal.valueOf(100.7)).pow(2);
        return BigDecimal.valueOf(Math.sqrt(latD2km.add(lonD2km).doubleValue()));
    }

    private Iterable<Log> generateLogsForJourney(Device device) {
        L.info("Generating logs for device " + device.getSerial() + "======");
        LEVEL[] levels = LEVEL.values();
        int totalLogs = random.nextInt(MAX_LOGS_JOURNEY);
        List<Log> logs = new ArrayList<>(totalLogs);
        for (int i = 0; i < totalLogs; i++) {
            Log log = new Log();
            {
                log.setDevice(device);
                log.setType(levels[random.nextInt(levels.length)]);
                log.setMessage("Message: " + log.getType().name());
            }
            L.debug(log.toString());
            logs.add(log);
        }
        if (WRITE_TO_DATABASE) {
            return logRepository.save(logs);
        } else {
            return logs;
        }
    }
}
