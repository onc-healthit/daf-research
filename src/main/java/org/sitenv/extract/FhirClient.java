package org.sitenv.extract;

import java.util.ArrayList;
import java.util.List;

import org.sitenv.client.HTTPHeaderInterceptor;
import org.sitenv.mapper.utilities.ConversionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.api.Header;

public class FhirClient {
	
	static final Logger logger = LoggerFactory.getLogger(FhirClient.class);
	
	private String serverBase;
	private FhirContext context;
	private IGenericClient client;
	
	public FhirClient(String s) {
		serverBase = s;
		context = FhirContext.forDstu2();
		
		//Invoke the client 
		client = context.newRestfulGenericClient(serverBase);
		client.registerInterceptor(new HTTPHeaderInterceptor());
		
	}
	
	public Bundle extractPatients(List<String> patientIds) {
	
		Bundle resultSet = new Bundle();
		for(String id : patientIds) {
			
			// Perform a search
			Bundle results = client
				.search()
				.forResource(Patient.class)
				      .where(Patient.RES_ID.matches().value(id))
				      .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
				      .execute();
			
			String s = context.newJsonParser().setPrettyPrint(true).encodeResourceToString(results);
			logger.debug(" Patient = " + s);
			
			for(Bundle.Entry ent : results.getEntry()) {		
				if(ent.getResource() instanceof Patient) {
					resultSet.addEntry(ent);
				}
			}
		}
		
		return resultSet;
	}

	public Bundle extractDataForPatient(List<String> patientIds) {
		
		Bundle resultSet = new Bundle();
		
		for(String id : patientIds) {
			
			// Create Id Data Type
			IdDt refId = new IdDt("Patient/" + id);
			
			// Extract Med Statement
			Bundle meds = client
				      .search()
				      .forResource(MedicationStatement.class)
				      .where(MedicationStatement.PATIENT.hasId(id))
				      .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
				      .execute();
			
			for(Bundle.Entry ent : meds.getEntry()) {	
				if(ent.getResource() instanceof MedicationStatement) {
					resultSet.addEntry(ent);
				}
			}
				
			// Extract Condition 
			Bundle conds = client
				      .search()
				      .forResource(Condition.class)
				      .where(Condition.PATIENT.hasId(id))
				      .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
				      .execute();
					
			for(Bundle.Entry ent : conds.getEntry()) {	
				if(ent.getResource() instanceof Condition) {
					logger.debug("Found Condition ");
					resultSet.addEntry(ent);
				}
			}	
		}
		
		String data = context.newJsonParser().setPrettyPrint(true).encodeResourceToString(resultSet);
		logger.debug(" Patient Data = " + data);
		System.out.println(" Patient Data = " + data);
		return resultSet;
	}
}
