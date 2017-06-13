package org.sitenv.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sitenv.mapper.utilities.ConversionUtilities;
import org.sitenv.mapper.utilities.MapperConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Patient;


public class ConditionMapper {

	static final Logger logger = LoggerFactory.getLogger(ConditionMapper.class);
	
	public static ArrayList<String> getLoadCmdsForPatientData(Bundle b) {
		
		ArrayList<String> cmds = new ArrayList<String>();
		
		for(Bundle.Entry ent : b.getEntry()) {
			
			logger.debug("Generating Commands ");
			if(ent.getResource() instanceof Condition) {
				

				Condition c = (Condition)ent.getResource();
				
				/*
				 * Need to construct an insert command as follows"
				 * INSERT into Condition VALUES 
				 * (ConditionID, PATID, EnounterId, ReportDate, ResolvedDate, OnsetDate, ConditionStatus, Condition, ConditionType, ConditionSource);
				 */
				String cmd = "insert into condition values (";
				
				// Add Id
				cmd += MapperConstants.SINGLE_QUOTE + c.getId().getIdPart() + MapperConstants.SINGLE_QUOTE_COMMA;
				
				// Add patient Id
				cmd += MapperConstants.SINGLE_QUOTE + c.getPatient().getReference().getIdPart() + MapperConstants.SINGLE_QUOTE_COMMA;
				
				// Add Encounter Id, since it is not mandatory or currently widely supported, we may not find it.
				ResourceReferenceDt en = c.getEncounter();
				if(en != null && en.getReference() != null) {
					String encId = en.getReference().getIdPart();
					
					if(encId != null ) {
						cmd += MapperConstants.SINGLE_QUOTE + encId + MapperConstants.SINGLE_QUOTE_COMMA;
					}
					else {
						cmd += MapperConstants.EMPTY_VALUE;
					}
				}
				
				
				// Add Reported/Recorded Date (In STU3 this will change to Asserter Date
				String rd = ConversionUtilities.convertFromFhirToSQLDate(c.getDateRecorded());
				if(rd != null) 
					cmd += MapperConstants.SINGLE_QUOTE + rd + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.DEF_DATE;
				
				// Add Resolved / Abatement Date, but this may not be present in all instances.
				String ad = ConversionUtilities.convertFromFhirIDataTypeToSQLDate(c.getAbatement());
				if(ad != null) 
					cmd += MapperConstants.SINGLE_QUOTE + ad + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.DEF_DATE;
				
				// Add Onset Date, but this may not be present in all instances.
				String od = ConversionUtilities.convertFromFhirIDataTypeToSQLDate(c.getOnset());
				if(od != null) 
					cmd += MapperConstants.SINGLE_QUOTE + od + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.DEF_DATE;
				
				// Add Condition Status
				String cs = ConversionUtilities.convertFromConditionClinicalStatusToCdm(c.getClinicalStatus());
				if(cs != null) 
					cmd += MapperConstants.SINGLE_QUOTE + cs + MapperConstants.SINGLE_QUOTE_COMMA;
				else 
					cmd += MapperConstants.EMPTY_VALUE;
				
				// Add Condition
				CodingDt cd = ConversionUtilities.getCodingFromCodeableConcept(c.getCode(), MapperConstants.SNOMED_CS);
				String code = ConversionUtilities.getCodeFromFhirCoding(cd);
				if(code != null) {
					cmd += MapperConstants.SINGLE_QUOTE + code + MapperConstants.SINGLE_QUOTE_COMMA;
				}
				else 
					cmd += MapperConstants.EMPTY_VALUE;
				
				// Add Condition Type
				String sys = ConversionUtilities.convertFhirCodeSystemToCdm(cd);
				if(sys != null) {
					cmd += MapperConstants.SINGLE_QUOTE + sys + MapperConstants.SINGLE_QUOTE_COMMA;
				}
				else 
					cmd += MapperConstants.EMPTY_VALUE;
				
				// Add Condition Source which is always Healthcare Problem List for a FHIR Source
				cmd += MapperConstants.SINGLE_QUOTE + MapperConstants.CONDITION_SOURCE + MapperConstants.SINGLE_QUOTE;
				
				cmd += MapperConstants.END_PAREN;
				
				cmds.add(cmd);
				
			}
		}
		
		return cmds;
	}
}
