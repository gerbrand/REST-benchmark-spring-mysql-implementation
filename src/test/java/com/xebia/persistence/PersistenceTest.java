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
import org.springframework.transaction.annotation.Transactional;

import com.xebia.rest.model.Record;

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
    
    @Test
    @Transactional(readOnly=false)
    public void persisting() {
        //long id, String shortStringAttribute, String longStringAttribute, int intNumber, boolean trueOrFalse) {
        Record r=new Record(1234,"Blah", "Blah blah",22,false);
        entityManager.persist(r);
        
        Record found=entityManager.find(Record.class, 1234);
        assertNotNull("Record is niet goed opgeslagen", found);
    }

}
