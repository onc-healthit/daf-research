package org.sitenv.mapper;

import java.util.ArrayList;
import java.util.List;

import org.sitenv.mapper.utilities.ConversionUtilities;
import org.sitenv.mapper.utilities.MapperConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;

public class PatientMapper {
	
	static final Logger logger = LoggerFactory.getLogger(PatientMapper.class);
	
	public static ArrayList<String> getLoadCmdsForPatient(Bundle b) {
		
		ArrayList<String> cmds = new ArrayList<String>();
		
		for(Bundle.Entry ent : b.getEntry()) {
			
			logger.debug("Generating Commands ");
			if(ent.getResource() instanceof Patient) {
				
				
				Patient p = (Patient)ent.getResource();
				
				/*
				 * Need to construct an insert command as follows"
				 * INSERT into DEMOGRAPHIC VALUES 
				 * (PATID, BIRTHDATE, BIRTHTIME, SEX, HISPANIC, RACE, BIOBANK_FLAG);
				 */
				String cmd = "insert into demographic values (";
				
				// Add Id
				cmd += MapperConstants.SINGLE_QUOTE + p.getId().getIdPart() + MapperConstants.SINGLE_QUOTE_COMMA;
				
				// Add Birth Date
				String dt = ConversionUtilities.convertFromFhirToSQLDate(p.getBirthDate());
				if(dt != null) 
					cmd += MapperConstants.SINGLE_QUOTE + dt + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.DEF_DATE;
				
				// No Birth Time extension has been specified by US Core, so skip the value
				cmd += MapperConstants.DEF_TIME;
				
				// Get US Core Birth Sex
				List<ExtensionDt> sexExt = p.getUndeclaredExtensionsByUrl(MapperConstants.FHIR_BIRTH_SEX_EXT_URL);
				String sex = ConversionUtilities.convertFhirSexToCdm(sexExt);
				
				if(sex != null) 
					cmd += MapperConstants.SINGLE_QUOTE + sex + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.EMPTY_VALUE;
				
				//Race
				List<ExtensionDt> raceExt = p.getUndeclaredExtensionsByUrl(MapperConstants.FHIR_RACE_EXT_URL);
				String race = ConversionUtilities.convertFhirRaceToCdm(raceExt);
				
				if(race != null) 
					cmd += MapperConstants.SINGLE_QUOTE + race + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.EMPTY_VALUE;
				
				// Ethnicity
				List<ExtensionDt> ethExt = p.getUndeclaredExtensionsByUrl(MapperConstants.FHIR_ETHNICITY_EXT_URL);
				String eth = ConversionUtilities.convertFhirEthnicityToCdm(ethExt);
				
				if(eth != null) 
					cmd += MapperConstants.SINGLE_QUOTE + eth + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.EMPTY_VALUE;
				
				// Add Biobank flag
				cmd += MapperConstants.LAST_EMPTY_VALUE;
				
				// close the statement
				cmd += MapperConstants.END_PAREN;
				
				cmds.add(cmd);
				
			}
		}
		
		logger.debug("Finished Generating Commands ");
		return cmds;
	}

}
