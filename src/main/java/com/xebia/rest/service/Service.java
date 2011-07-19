package com.xebia.rest.service;

import java.io.*;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xebia.rest.model.Record;

@Controller
public class Service {
    private static final Logger log=LoggerFactory.getLogger(Service.class);
    
    private static final int FLUSH_THRESHOLD=20;
    @PersistenceContext
    private EntityManager em;
    

    @PostConstruct
    public void buildDatabase() throws FileNotFoundException {
        EntityTransaction tx = em.getTransaction();
 
        File dataFile = new File("/tmp/data.json");
        if (dataFile.exists()) {
            tx.begin();
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            int lines=0;
            long start=System.currentTimeMillis();
            try {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    ObjectMapper mapper = new ObjectMapper();
                    Record record = mapper.readValue(line, Record.class);
                    em.persist(record);
                    lines++;
                    if (lines%FLUSH_THRESHOLD==0) {
                        tx.commit(); //in between commits
                        log.debug("Wrote "+lines+" lines");
                    }
                }
            
                tx.commit();
            } catch (IOException e) {
                log.error("Got "+e.getMessage()+" after "+lines+" lines.");
                tx.rollback();
                throw new RuntimeException(e);
            } finally {
                long elapsed=(System.currentTimeMillis()-start)/1000l;
                log.info("Finished reading after "+elapsed+" seconds");
            }
        }
    }
    
    
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public @ResponseBody Record get(@PathVariable long id, HttpServletResponse response) throws IOException {
    	Record result = em.find(Record.class, id);
    	if (result == null) {
    		response.sendError(404, "Record with id=" + id + " is not in database.");
    		return null;
    	} else {
    		return result;
    	}
	}
    
    @RequestMapping(value = "/put/{id}", method = RequestMethod.POST)
    public void put(@PathVariable long id, @RequestBody Record record, HttpServletResponse response) throws IOException {
    	
        if (record.getId() != id) {
    		response.sendError(409, "The resource ID and ID of the POSTed record do not match.");
    	} else {
    		Record savedRecord = em.merge(record);
    	}
    }
    
    @RequestMapping(value = "/post/{id}", method = RequestMethod.POST)
    public void post(@PathVariable long id, @RequestBody Record record, HttpServlet response) {
        em.persist(record);
    }
}
