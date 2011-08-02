package com.xebia.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.xebia.rest.model.Record;

/**
 * Created by Frank Visser User: frank Date: 7/19/11 Time: 7:14 PM
 */
@Component
public class DatabaseLoader {
    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);
    private static final int COMMIT_THRESHOLD = 250; // what would an optional
                                                     // value be?
    @PersistenceContext
    private EntityManager em;
    @Resource
    private PlatformTransactionManager txManager;

    public void importData(File dataFile) throws FileNotFoundException {

        log.info("Going to read " + dataFile.getAbsoluteFile());
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        int lines = 0;
        long start = System.currentTimeMillis();

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // explicitly setting the transaction name is something that can only be
        // done programmatically
        def.setName("importTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = txManager.getTransaction(def);

        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                ObjectMapper mapper = new ObjectMapper();
                Record record = mapper.readValue(line, Record.class);
                em.merge(record);
                lines++;
                if (lines % COMMIT_THRESHOLD == 0) {
                    txManager.commit(status); // in between commits
                    log.debug("Wrote " + lines + " lines");
                }
            }
            if (lines % COMMIT_THRESHOLD != 0) {
                txManager.commit(status);
            }
            log.info("File " + dataFile.getAbsolutePath() + " is imported, you may remove the file.");
            // dataFile.renameTo(new
            // File(dataFile.getParentFile(),"data"+DateFormat.getDateTimeInstance(DateFormat.SHORT,
            // DateFormat.SHORT).format(new Date())+".json"));
        } catch (IOException e) {
            log.error("Got " + e.getMessage() + " after " + lines + " lines.");
            txManager.rollback(status);
            throw new RuntimeException(e);
        } finally {
            long elapsed = (System.currentTimeMillis() - start) / 1000l;
            log.info("Finished reading after " + elapsed + " seconds");
        }

    }

    public static void main(String args[]) throws FileNotFoundException {
        ApplicationContext context = new ClassPathXmlApplicationContext("/StandAloneApplicationContext.xml");
        final String f;
        if (args.length>0) {
            f=args[0];
        } else {
            f="src/main/resources/data.json";
        }
        DatabaseLoader loader = context.getBean(DatabaseLoader.class);
        File dataFile = new File(f);
        loader.importData(dataFile);
    }

}
