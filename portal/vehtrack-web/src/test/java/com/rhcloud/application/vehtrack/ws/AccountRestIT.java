package com.rhcloud.application.vehtrack.ws;

import com.rhcloud.application.vehtrack.ws.resources.AccountResource;
import com.jayway.restassured.RestAssured;
import static com.jayway.restassured.RestAssured.given;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import com.rhcloud.application.vehtrack.domain.ROLE;
import java.io.IOException;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountRestIT {
    
    private static final String ACCOUNT_ENDPOINT = "http://localhost:10000/account/";
    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_NO_CONTENT = 204;
    private static final int HTTP_NOT_FOUND = 404;
    private static final Logger L = LoggerFactory.getLogger(ValidateRestIT.class);
    
    private RequestSpecification auth() {
        return given().auth().digest(user, pass);
    }
    private String user = "admin";
    private String pass = "hackme";
    
    @Before
    public void setUp() {
        L.info("Account endpoint is up and running");
        verify_endpoint();
    }
    
    @Test
    public void account_crud_sample() {
        String expectedId = "integrationTest";
        AccountResource expectedAccount = new AccountResource();
        {
            expectedAccount.setLogin(expectedId);
            expectedAccount.setPassword("changeme");
            expectedAccount.setRoles(Arrays.asList(ROLE.USER, ROLE.FLEET_ADMIN));
        }
        AccountResource actualAccount;
        try {
            L.info("Create an account");
            String actualId = create_an_account(expectedAccount);
            assertEquals(expectedId, actualId);
            
            L.info("Read an account");
            actualAccount = read_an_account(expectedId);
            //! login is missing from the response cuz it's in the id
            assertEquals(expectedAccount.getPassword(), actualAccount.getPassword());
            
            L.info("Update an account");
            //! also works without specifying all parameters (like here by reusing the object) just the ones who need update.
            expectedAccount.setPassword("whatever");
            update_an_account(expectedAccount, expectedId);
            
            L.info("Read an account");
            actualAccount = read_an_account(expectedId);
            assertEquals(expectedAccount.getPassword(), actualAccount.getPassword());
            
            L.info("Delete an account");
            delete_an_account(expectedId);
            
            L.info("Read an account");
            non_exisiting_account(expectedId);
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        finally {
            RestAssured.reset();
        }
    }
    
    public void verify_endpoint() {
        auth().expect().statusCode(HTTP_OK).when().get(ACCOUNT_ENDPOINT);
    }
    
    public String create_an_account(AccountResource account) throws IOException {
        JsonPath jsonPath = auth().given().contentType(ContentType.JSON).body(RestUtil.toJson(account)).expect().statusCode(HTTP_CREATED).when().post("http://localhost:10000/account?returnBody=true").body().jsonPath();
        return RestUtil.getEntityID(jsonPath);
    }
    
    public AccountResource read_an_account(String id) throws IOException {
        String jsonBody = auth().expect().statusCode(HTTP_OK).when().get(ACCOUNT_ENDPOINT + id).body().print();
        return (AccountResource) RestUtil.fromJson(jsonBody, AccountResource.class);
    }
    
    public void update_an_account(AccountResource account, String id) throws IOException {
        auth().given().contentType(ContentType.JSON).body(RestUtil.toJson(account)).expect().statusCode(HTTP_NO_CONTENT).when().put(ACCOUNT_ENDPOINT + id);
    }
    
    public void delete_an_account(String id) {
        auth().expect().statusCode(HTTP_NO_CONTENT).when().delete(ACCOUNT_ENDPOINT + id);
    }
    
    public void non_exisiting_account(String id) {
        auth().expect().statusCode(HTTP_NOT_FOUND).when().get(ACCOUNT_ENDPOINT + id);
    }
}
