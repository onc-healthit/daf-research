package org.sitenv.extract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.sitenv.client.MainClient;
import org.sitenv.dao.DBInterface;
import org.sitenv.dao.DBSchemaManager;
import org.sitenv.dao.derbyimpl.DBInterfaceImpl;
import org.sitenv.dao.derbyimpl.DBSchemaManagerImpl;
import org.sitenv.mapper.FhirToPcorNetCdmMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.model.dstu2.resource.Bundle;

public class EtlManager {
	
	static final Logger logger = LoggerFactory.getLogger(EtlManager.class);
	
	private static final String PATIENT_LIST_PROP = ".patients";
	
	private String serverBase;
	private String patientListFile;
	private String dbUrl;
	private String serverName;
	
	private Bundle patientBundle;
	private Bundle patientData;
	DBInterface dbInterface;
	
	public EtlManager(String sn, String sb, String plFile, String db) {
		serverName = sn;
		serverBase = sb;
		patientListFile = plFile;
		dbUrl = db;
		
		dbInterface = new DBInterfaceImpl(dbUrl);
	}
	
	public void runETLInOneShot() {
		

		DBInterface db = new DBInterfaceImpl(dbUrl);
		
		System.out.println(" Starting Extraction for Server ... " + serverBase);
		
		FhirClient client = new FhirClient(serverBase);
		
		// Get Patient List, This would be maintained external to this program typically.
		List<String> patients = getPatientList();
		
		// Extract the patient data seperately since they need to be inserted before others into the system.
		if(patients != null) {
			Bundle patientBundle = client.extractPatients(patients);
			
			// Extract the Other Data Elements for each of the patients
			Bundle patientData = client.extractDataForPatient(patients);
			
			System.out.println(" Finished Extraction Successfully ");
			
			
			// Transform data from FHIR to PCORnet CDM
			System.out.println(" Start Transformation from FHIR to PCORnet CDM ");
			
			// Transform Patient Data to PCORnet CDM format.
			ArrayList<String> cmds = FhirToPcorNetCdmMap.getLoadCmdsForPatient(patientBundle);
			
			ArrayList<String> dataCmds = FhirToPcorNetCdmMap.getLoadCmdsForPatientData(patientData);
			
			System.out.println(" Finished Transformation from FHIR to PCORnet CDM ");
			
			System.out.println(" Load the patient data");
			
			// Load the Patients.
			for(String cmd : cmds) {
				logger.debug(" Executing command " + cmd);
				System.out.println(" Executing command " + cmd);
				db.executeUpdate(cmd);
			}
			
			// Load Patient Data (Conditions/Meds etc)
			for(String dc : dataCmds) {
				logger.debug(" Executing command " + dc);
				System.out.println(" Executing command " + dc);
				db.executeUpdate(dc);
			}
			
			System.out.println("Finished loading data");
			
		}
		else {
			System.out.println(" Nothing to do, because patient list is null");
		}
		
		db.closeConnection();

    }
	
	public void runExtract() {
		
		System.out.println(" Starting Extraction for Server ... " + serverBase);
		
		FhirClient client = new FhirClient(serverBase);
		
		// Get Patient List, This would be maintained external to this program typically.
		List<String> patients = getPatientList();
		
		// Extract the patient data seperately since they need to be inserted before others into the system.
		if(patients != null) {
			
			patientBundle = client.extractPatients(patients);
			
			// Extract the Other Data Elements for each of the patients
			patientData = client.extractDataForPatient(patients);
			
			System.out.println(" Finished Extraction Successfully ");
			
		}
		else {
			
			System.out.println(" Cannot extract because there are no patients ");
		}
	}
	
	public void runTransformAndLoad() {
		
		// Transform data from FHIR to PCORnet CDM
		System.out.println(" Start Transformation from FHIR to PCORnet CDM ");

		// Transform Patient Data to PCORnet CDM format.
		ArrayList<String> cmds = FhirToPcorNetCdmMap.getLoadCmdsForPatient(patientBundle);

		ArrayList<String> dataCmds = FhirToPcorNetCdmMap.getLoadCmdsForPatientData(patientData);

		System.out.println(" Finished Transformation from FHIR to PCORnet CDM ");

		System.out.println(" Load the patient data");

		// Load the Patients.
		for(String cmd : cmds) {
			logger.debug(" Executing command " + cmd);
			System.out.println(" Executing command " + cmd);
			dbInterface.executeUpdate(cmd);
		}

		// Load Patient Data (Conditions/Meds etc)
		for(String dc : dataCmds) {
			logger.debug(" Executing command " + dc);
			System.out.println(" Executing command " + dc);
			dbInterface.executeUpdate(dc);
		}

		System.out.println("Finished loading data");

		
	}
	
	public List<String> getPatientList() {
		
		Properties prop = new Properties();
		List<String> patients = null;
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(patientListFile));
			
			String sn = serverName + PATIENT_LIST_PROP;
			
			if(prop.containsKey(sn) ) {
				
				patients = new ArrayList<String>();
				
				String patList = prop.getProperty(sn);
				
				String[] patientList = patList.split(",");
				
				patients = (List<String>) Arrays.asList(patientList);
				
			}
			
		}
		catch (IOException e) {
				
			e.printStackTrace();
		}
		
		return patients;
	}
}
