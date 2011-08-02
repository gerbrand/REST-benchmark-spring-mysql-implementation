package com.xebia.rest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xebia.rest.model.Record;

@Controller
public class Service {
    private static final Logger log=LoggerFactory.getLogger(Service.class);
    
    private static final int COMMIT_THRESHOLD=250; //what would an optional value be?
    @PersistenceContext
    private EntityManager em;
    @Resource
    private PlatformTransactionManager txManager;

    @PostConstruct
    public void buildDatabase() throws Exception {
        
        File dataFile = new File("/tmp/data.json");
        if (dataFile.exists()) {
            log.info("Going to read "+dataFile.getAbsoluteFile());
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            int lines=0;
            long start=System.currentTimeMillis();

            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
         // explicitly setting the transaction name is something that can only be done programmatically
         def.setName("importTx");
         def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

         TransactionStatus status = txManager.getTransaction(def);
         
         try {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    ObjectMapper mapper = new ObjectMapper();
                    Record record = mapper.readValue(line, Record.class);
                    em.merge(record);
                    lines++;
                    if (lines%COMMIT_THRESHOLD==0) {
                        txManager.commit(status); //in between commits
                        log.debug("Wrote "+lines+" lines");
                    }
                }
                if (lines%COMMIT_THRESHOLD!=0) {
                    txManager.commit(status);
                }
                log.info("File "+dataFile.getAbsolutePath()+" is imported, you may remove the file.");
//                dataFile.renameTo(new File(dataFile.getParentFile(),"data"+DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date())+".json"));
            } catch (IOException e) {
                log.error("Got "+e.getMessage()+" after "+lines+" lines.");
                txManager.rollback(status);
                throw new RuntimeException(e);
            } finally {
                long elapsed=(System.currentTimeMillis()-start)/1000l;
                log.info("Finished reading after "+elapsed+" seconds");
            }
        } else {
            log.info("If you want to read initial data, create a file named "+dataFile.getAbsolutePath());
        }
    }
    
    
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @Transactional(readOnly=true)
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
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void put(@PathVariable long id, @RequestBody Record record, HttpServletResponse response) throws IOException {
    	
        if (record.getId() != id) {
    		response.sendError(409, "The resource ID and ID of the POSTed record do not match.");
    	} else {
    		em.merge(record);
    	}
    }
    
    @RequestMapping(value = "/post/{id}", method = RequestMethod.POST)
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void post(@PathVariable long id, @RequestBody Record record, HttpServletResponse response) throws IOException {
        try {
            em.persist(record);
        } catch (EntityExistsException e) {
            response.sendError(500, "The resource "+id+" already exists");
        }
    }
    
    public static void main(String[] args) {
        
    }
}
