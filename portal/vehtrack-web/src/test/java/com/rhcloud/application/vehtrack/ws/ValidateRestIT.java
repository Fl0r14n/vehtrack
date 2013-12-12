package com.rhcloud.application.vehtrack.ws;

import com.rhcloud.application.vehtrack.ws.resources.EventResource;
import com.rhcloud.application.vehtrack.ws.resources.DeviceResource;
import com.rhcloud.application.vehtrack.ws.resources.AccountResource;
import com.jayway.restassured.RestAssured;
import org.junit.Test;
import static com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import com.rhcloud.application.vehtrack.domain.ROLE;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To run these tests the server must be running (mvn t7:run)
 */
public class ValidateRestIT {

    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_NO_CONTENT = 204;
    private static final int HTTP_NOT_ALLOWED = 401;
    private static final Logger L = LoggerFactory.getLogger(ValidateRestIT.class);

    private RequestSpecification auth() {
        return given().auth().digest(user, pass);
    }
    private String user = "admin";
    private String pass = "hackme";

    @Before
    public void setUp() {
        L.info("ENDPOINT RUNNING");
        root_endpoint_should_fail_wo_auth();
        root_endpoint_should_work_on_md5_auth();
        verify_that_account_endpoint_is_working();
        verify_that_device_endpoint_is_working();
        verify_that_event_endpoint_is_working();
    }

    @After
    public void tearDown() {
        L.info("DELETE CREATED OBJECTS");
        event_endpoint_should_delete_event(eventId);
        device_endpoint_should_delete_device(deviceId);
    }
    private String deviceId;
    private String eventId;
    private String accountId;

    @Test
    public void crud_test_suite() {
        final AccountResource expectedAccount = new AccountResource();
        {
            expectedAccount.setLogin("login");
            expectedAccount.setPassword("password");
            expectedAccount.setRoles(Arrays.asList(ROLE.ADMIN));
        }
        final DeviceResource expectedDevice = new DeviceResource();
        {
            expectedDevice.setSerial("foo");
            expectedDevice.setEmail("bar");
        }
        final EventResource expectedEvent = new EventResource();
        {
            expectedEvent.setRecordedTimestamp(new Date());
            expectedEvent.setType(1);
            expectedEvent.setMessage("Hello from Pojo");
        }

        DeviceResource actualDevice;
        EventResource actualEvent;
        AccountResource actualAccount;
        try {
            L.info("CREATE AN ACCOUNT");
            accountId = account_endpoint_should_add_a_new_account(expectedAccount);
            actualAccount = account_endpoint_should_read_the_account(accountId);
            actualAccount.setLogin(expectedAccount.getLogin()); //the json response does not contain the id
            assertEquals(expectedAccount, actualAccount);

            L.info("CREATE A DEVICE");
            expectedDevice.setAccount(RestUtil.addLink(actualAccount, ""));
            deviceId = device_endpoint_should_add_new_device(expectedDevice);
            actualDevice = device_endpoint_should_read_the_device(deviceId);
            assertEquals(expectedDevice.getEmail(), actualDevice.getEmail());

            L.info("UPDATE THE DEVICE");
            expectedDevice.setSerial("foo_" + deviceId);
            expectedDevice.setEmail("bar_" + deviceId);
            device_endpoint_should_update_device(expectedDevice, deviceId);
            actualDevice = device_endpoint_should_read_the_device(deviceId);
            assertEquals(expectedDevice.getEmail(), actualDevice.getEmail());

            L.info("CREATE AN EVENT");
            expectedEvent.setDevice(RestUtil.addLink(actualDevice, "event.Device"));
            eventId = event_endpoint_shoud_add_a_new_event_directly_from_domain_object(expectedEvent, deviceId);
            actualEvent = event_endpoint_should_read_the_event(eventId);
            assertEquals(expectedEvent.getRecordedTimestamp(), actualEvent.getRecordedTimestamp());

            L.info("UPDATE THE EVENT");
            expectedEvent.setRecordedTimestamp(new Date());
            expectedEvent.setType(2);
            expectedEvent.setMessage("Hello again from Pojo_" + eventId);
            event_endpoint_shoud_update_an_event_directly_from_domain_object(expectedEvent, eventId);
            actualEvent = event_endpoint_should_read_the_event(eventId);
            assertEquals(expectedEvent.getRecordedTimestamp(), actualEvent.getRecordedTimestamp());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        finally {
            RestAssured.reset();
        }
    }

    public void root_endpoint_should_fail_wo_auth() {
        L.info("root_endpoint_should_fail_wo_auth");
        expect().statusCode(HTTP_NOT_ALLOWED).when().get("http://localhost:10000/");
        L.info("=============================================================");
    }

//    @Ignore //seems to be a problem with rest easy: altough I selected basic it will attempt digest
//    public void root_endpoint_should_fail_on_basic_auth() {
//        L.info("root_endpoint_should_fail_on_basic_auth");
//        given().auth().basic("admin", "hackme").expect().statusCode(HTTP_NOT_ALLOWED).when().get("http://localhost:10000/");
//        L.info("=============================================================");
//    }

    public void root_endpoint_should_work_on_md5_auth() {
        L.info("root_endpoint_should_work_on_md5_auth");
        auth().expect().statusCode(HTTP_OK).when().get("http://localhost:10000/");
        L.info("=============================================================");
    }

    public void verify_that_account_endpoint_is_working() {
        auth().expect().statusCode(HTTP_OK).when().get("http://localhost:10000/account");
    }

    public void verify_that_device_endpoint_is_working() {
        auth().expect().statusCode(HTTP_OK).when().get("http://localhost:10000/device");
    }

    public void verify_that_event_endpoint_is_working() {
        auth().expect().statusCode(HTTP_OK).when().get("http://localhost:10000/event");
    }

    //--------------------------------------------------------------------------
    public String account_endpoint_should_add_a_new_account(AccountResource account) throws IOException {
        JsonPath jsonPath = auth().given().contentType(ContentType.JSON).body(RestUtil.toJson(account)).expect().statusCode(HTTP_CREATED).when().post("http://localhost:10000/account?returnBody=true").body().jsonPath();
        return RestUtil.getEntityID(jsonPath);
    }

    public AccountResource account_endpoint_should_read_the_account(String id) throws IOException {
        String jsonBody = auth().expect().statusCode(HTTP_OK).when().get("http://localhost:10000/account/" + id).body().print();
        return (AccountResource) RestUtil.fromJson(jsonBody, AccountResource.class);
    }

    //--------------------------------------------------------------------------
    public String device_endpoint_should_add_new_device(DeviceResource device) throws IOException {
        JsonPath jsonPath = auth().given().contentType(ContentType.JSON).body(RestUtil.toJson(device)).expect().statusCode(HTTP_CREATED).when().post("http://localhost:10000/device?returnBody=true").body().jsonPath();
        return RestUtil.getEntityID(jsonPath);
    }

    public DeviceResource device_endpoint_should_read_the_device(String id) throws IOException {
        String jsonBody = auth().expect().statusCode(HTTP_OK).when().get("http://localhost:10000/device/" + id).body().print();
        return (DeviceResource) RestUtil.fromJson(jsonBody, DeviceResource.class);
    }

    public void device_endpoint_should_update_device(DeviceResource device, String id) throws IOException {
        auth().given().contentType(ContentType.JSON).body(RestUtil.toJson(device)).expect().statusCode(HTTP_NO_CONTENT).when().put("http://localhost:10000/device/" + id);
    }

    public void device_endpoint_should_delete_device(String id) {
        auth().expect().statusCode(HTTP_NO_CONTENT).when().delete("http://localhost:10000/device/" + id);
    }

    //--------------------------------------------------------------------------
    public String event_endpoint_shoud_add_a_new_event_directly_from_domain_object(EventResource event, String deviceId) throws IOException {
        String json = RestUtil.toJson(event);
        JsonPath jsonPath = auth().given().contentType(ContentType.JSON).body(json).expect().statusCode(HTTP_CREATED).when().post("http://localhost:10000/event?returnBody=true").body().jsonPath();
        return RestUtil.getEntityID(jsonPath);
    }

    public EventResource event_endpoint_should_read_the_event(String id) throws IOException {
        String jsonBody = auth().expect().statusCode(HTTP_OK).when().get("http://localhost:10000/event/" + id).body().print();
        return (EventResource) RestUtil.fromJson(jsonBody, EventResource.class);
    }

    public void event_endpoint_shoud_update_an_event_directly_from_domain_object(EventResource event, String id) throws IOException {
        auth().given().contentType(ContentType.JSON).body(RestUtil.toJson(event)).expect().statusCode(HTTP_NO_CONTENT).when().put("http://localhost:10000/event/" + id);
    }

    public void event_endpoint_should_delete_event(String id) {
        auth().expect().statusCode(HTTP_NO_CONTENT).when().delete("http://localhost:10000/event/" + id);
    }
}
