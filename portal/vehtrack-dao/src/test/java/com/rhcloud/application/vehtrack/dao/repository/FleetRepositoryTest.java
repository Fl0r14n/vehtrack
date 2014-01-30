package com.rhcloud.application.vehtrack.dao.repository;

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
public class FleetRepositoryTest {

    @Autowired
    private FleetRepository fleetRepository;
    
    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }
    
    @Test
    public void save_should_persist_a_fleet() {
        
    }
}
