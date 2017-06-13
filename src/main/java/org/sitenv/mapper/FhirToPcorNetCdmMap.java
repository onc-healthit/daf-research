package org.sitenv.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sitenv.mapper.utilities.ConversionUtilities;
import org.sitenv.mapper.utilities.MapperConstants;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;

public class FhirToPcorNetCdmMap {
	
	private HashMap<String, ArrayList<String> > tableToResourceMap;
	
	public FhirToPcorNetCdmMap() {
		
		tableToResourceMap = new HashMap<String, ArrayList<String> >();
	}
	
	public static ArrayList<String> getLoadCmdsForPatient(Bundle b) {
		
		return PatientMapper.getLoadCmdsForPatient(b);
	}
	
	public static ArrayList<String> getLoadCmdsForPatientData(Bundle b) {
		
		ArrayList<String> returnCmds = new ArrayList<String>();
		
		returnCmds.addAll(ConditionMapper.getLoadCmdsForPatientData(b));
		
		return returnCmds;
	}
}
