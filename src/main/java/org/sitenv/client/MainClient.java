package org.sitenv.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.sitenv.dao.DBInterface;
import org.sitenv.dao.DBSchemaManager;
import org.sitenv.dao.derbyimpl.DBInterfaceImpl;
import org.sitenv.dao.derbyimpl.DBSchemaManagerImpl;
import org.sitenv.extract.EtlManager;
import org.sitenv.extract.FhirClient;
import org.sitenv.mapper.FhirToPcorNetCdmMap;
import org.sitenv.workflow.WorkflowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.model.dstu2.resource.Bundle;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class MainClient {

	static final Logger logger = LoggerFactory.getLogger(MainClient.class);
	
	@Parameter(names={"--server", "-s"}, description = "The server to extract data from.", required=true)
	private String serverName;
	
	private static final String CONFIG_FILE_PROP = "application.properties";
	private static final String SERVER_BASE_PROP = ".server.baseurl";
	private static final String PATIENT_LIST_FILE_PROP = "patientlist";
	private static final String DB_URL_PROP = "dburl";

	private String serverBase;
	private String patientListFile;
	private String dbUrl;
	
	public static void main(String[] args) {
		MainClient main = new MainClient();
	    new JCommander(main, args);
	    
	    main.run();
	}

	public void run() {
		
		loadConfiguration(serverName);
		
		EtlManager etl = new EtlManager(serverName, serverBase, patientListFile, dbUrl);
		WorkflowManager wm = new WorkflowManager(serverName, serverBase, patientListFile, dbUrl, etl);
		wm.initialize();
		
		// C1 : Step 1
		wm.createExtractionTask();
		
		// C1: Step 2
		wm.executeExtractionTask();
		
		// C1 : Step 3
		wm.createLoadTask();
				
		// C1: Step 4
		wm.executeLoadTask();

		
	}

	
	public void loadConfiguration(String sn) {
		
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_PROP));
			
			String sb = sn + SERVER_BASE_PROP;
			if(prop.containsKey(sb) ) {			
				logger.debug(" Server Base = " + prop.getProperty(sb));
				serverBase = prop.getProperty(sb);
			}
				
			if(prop.containsKey(PATIENT_LIST_FILE_PROP) ) {			
				logger.debug(" patient list file = " + prop.getProperty(PATIENT_LIST_FILE_PROP));
				patientListFile = prop.getProperty(PATIENT_LIST_FILE_PROP);
			}
			
			if(prop.containsKey(DB_URL_PROP) ) {			
				logger.debug(" DB URL = " + prop.getProperty(DB_URL_PROP));
				dbUrl = prop.getProperty(DB_URL_PROP);
			}
			
			
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
