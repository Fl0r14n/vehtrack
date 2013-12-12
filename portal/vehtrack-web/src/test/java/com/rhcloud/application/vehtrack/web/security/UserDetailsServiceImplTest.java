package com.rhcloud.application.vehtrack.web.security;

import com.rhcloud.application.vehtrack.dao.repository.AccountRepository;
import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.ROLE;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.security.core.userdetails.UserDetailsService;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:repository-context.xml")
public class UserDetailsServiceImplTest {
    
    UserDetailsService userDetailsService;
    @Autowired
    AccountRepository accountRepo;
    
    @Before
    public void setUp() {
        expected = new Account();
        {
            expected.setLogin(login);            
            expected.setPassword(password);
            expected.setRoles(Arrays.asList(ROLE.ADMIN));
        }
        accountRepo.save(expected);
    }
    private String login = "foo";
    private String password = "bar";
    private Account expected;
    
    @After
    public void tearDown() {
        accountRepo.delete(login);
    }
    
    @Test
    public void loadUserByUsername_should_find_the_user() {
        userDetailsService = new UserDetailsServiceImpl();
        UserDetails userDetails = userDetailsService.loadUserByUsername(login);
        assertNull(userDetails);
    }
}
