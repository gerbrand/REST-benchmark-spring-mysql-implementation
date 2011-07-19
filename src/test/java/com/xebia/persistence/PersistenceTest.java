package com.xebia.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"} )
public class PersistenceTest {

    @PersistenceContext
    private EntityManager entityManager;


    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void doesJPAWork() {
        assertNotNull(entityManagerFactory);
        assertNotNull(entityManager);
    }

}
