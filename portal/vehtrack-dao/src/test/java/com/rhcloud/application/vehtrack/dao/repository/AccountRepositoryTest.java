package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.ROLE;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:repository-context.xml")
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepo;

    @Before
    public void setUp() {
        expected = new Account();
        {
            expected.setLogin("foo");
            expected.setPassword("bar");
            expected.setRoles(new HashSet<>(Arrays.asList(ROLE.ADMIN, ROLE.DEVICE, ROLE.USER)));
        }
    }
    private Account expected;
    
    @After
    public void tearDown() {
        accountRepo.deleteAll();
    }
    
    @Test
    public void save_should_persist_an_account() {        
        accountRepo.save(expected);
        Account actual = accountRepo.findOne("foo");
        assertEquals(expected, actual);
    }
}
