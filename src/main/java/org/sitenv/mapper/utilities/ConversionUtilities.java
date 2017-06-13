package org.sitenv.mapper.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;

public class ConversionUtilities {
	
	static final Logger logger = LoggerFactory.getLogger(ConversionUtilities.class);

	public static String convertFromFhirToSQLDate(Date dt) {
		
		if(dt != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String s = dateFormat.format(dt);
			
			return s;
		}
		else {
			return null;
		}
	}
	
	public static String convertFromFhirIDataTypeToSQLDate(IDatatype dt) {
		
		if(dt != null && dt instanceof Date) {
			Date dd = (Date) dt;
			return ConversionUtilities.convertFromFhirToSQLDate(dd);
		}
		else {
			return null;
		}
		
	}
	
	public static CodingDt getCodingFromCodeableConcept(CodeableConceptDt cd, String preferredCodeSystem) {
		CodingDt retVal = null;
		
		if(cd != null ) {
			
			for(CodingDt code : cd.getCoding()) {
				
				if( (code.getSystem() != null) && (code.getSystem().equalsIgnoreCase(preferredCodeSystem))) {
					
					retVal = code;
					break;
				}
			}
			
			if(retVal == null) {
				retVal = cd.getCodingFirstRep();	
			}
		}

		return retVal;
	}
	
	public static String getCodeFromFhirCoding(CodingDt cd) {
		
		if(cd != null ) {			
			return cd.getCode();
		}
		else 
			return null;
	}
	
	public static String convertFhirCodeSystemToCdm(CodingDt cd) {
		
		if(cd != null && cd.getSystem() != null) {			
			
			String system = cd.getSystem();
			
			if(system.equalsIgnoreCase(MapperConstants.SNOMED_CS)) {
				return "SM";
			}
			
			
		}
		
		return "UN";
	}

	
	public static String convertFhirSexToCdm(List<ExtensionDt> ext) {
		
		for(ExtensionDt ex: ext) {

			if(ex.getValue() instanceof CodeableConceptDt) {
				CodeableConceptDt val = (CodeableConceptDt) ex.getValue();
				CodingDt cd = val.getCodingFirstRep();
				
				if(cd != null && (cd.getCode().equalsIgnoreCase("M") || cd.getCode().equalsIgnoreCase("F")))
					return cd.getCode();
				else
					return "UN";
			}
		}
		
		return null;
	}
	
	public static String convertFhirRaceToCdm(List<ExtensionDt> ext) {
		
		for(ExtensionDt ex: ext) {

			if(ex.getValue() instanceof CodeableConceptDt) {
				CodeableConceptDt val = (CodeableConceptDt) ex.getValue();
				
				if(val.getCoding() != null && val.getCoding().size() > 1) {
					return "06";
				}
				else {
					CodingDt cd = val.getCodingFirstRep();
					
					if(cd != null && (cd.getCode().equalsIgnoreCase("1002-5")))
						return "01";
					else if(cd != null && (cd.getCode().equalsIgnoreCase("2028-9")))
						return "02";
					else if(cd != null && (cd.getCode().equalsIgnoreCase("2054-5")))
						return "03";
					else if(cd != null && (cd.getCode().equalsIgnoreCase("2076-8")))
						return "04";
					else if(cd != null && (cd.getCode().equalsIgnoreCase("2106-3")))
						return "05";
					else if(cd != null && (cd.getCode().equalsIgnoreCase("UNK")))
						return "UN";
					else
						return "OT";
				}
			}
		}
		
		// Otherwise No information
		return "NI";
	}
	
	public static String convertFhirEthnicityToCdm(List<ExtensionDt> ext) {
		
		for(ExtensionDt ex: ext) {

			if(ex.getValue() instanceof CodeableConceptDt) {
				CodeableConceptDt val = (CodeableConceptDt) ex.getValue();
				
				CodingDt cd = val.getCodingFirstRep();
					
					if(cd != null && (cd.getCode().equalsIgnoreCase("2135-2")))
						return "Y";
					else if(cd != null && (cd.getCode().equalsIgnoreCase("2186-5")))
						return "N";
					else
						return "OT";
			}
		}
		
		return "NI";
	}
	
	public static String convertFromConditionClinicalStatusToCdm(String cs) {
		
		if(cs != null && cs.equalsIgnoreCase("active")) {
			return "AC";
		}
		else if(cs != null && cs.equalsIgnoreCase("inactive")){
			return "IN";
		}
		else if(cs != null && cs.equalsIgnoreCase("resolved")){
			return "RS";
		}
		else if (cs != null){
			return "OT";
		}
		else {
			return "UN";
		}
	
	}
}
