package com.xebia.rest.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xebia.rest.model.Record;

@Controller
public class Service {
	private static final Map<Long, Record> db;
	static {
		db = new HashMap<Long, Record>();
		BufferedReader br = new BufferedReader(new InputStreamReader(Service.class.getResourceAsStream("/data.json")));
		try {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				ObjectMapper mapper = new ObjectMapper();
				Record record = mapper.readValue(line, Record.class);
				db.put(record.getId(), record);
			}
		} catch (IOException e) {
			System.err.println("Could not load database from classpath. Exiting.");
			System.exit(1);
		}
	}
	
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public @ResponseBody Record get(@PathVariable long id, HttpServletResponse response) throws IOException {
    	Record result = db.get(id);
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
    		db.put(id, record);
    	}
    }
}
