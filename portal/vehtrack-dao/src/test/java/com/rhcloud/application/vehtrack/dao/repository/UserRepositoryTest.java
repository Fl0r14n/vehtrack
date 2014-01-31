package com.rhcloud.application.vehtrack.dao.repository;

import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.ROLE;
import com.rhcloud.application.vehtrack.domain.User;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:repository-context.xml")
public class UserRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void save_should_persist_a_user() {
        Account account = new Account();
        {
            account.setLogin("admin");
            account.setPassword("hackme");
            account.setRoles(new HashSet<>(Arrays.asList(ROLE.ADMIN)));
        }
        account = accountRepository.save(account);
        User expected = new User();
        {
            expected.setAccount(account);
            expected.setEmail("admin@vehtrack.com");
            expected.setName("Administrator");
        }
        userRepository.save(expected);

        User actual = userRepository.findOne(1L);
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
        Assert.assertEquals(expected.getAccount(), actual.getAccount());
    }
}
