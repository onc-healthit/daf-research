package org.sitenv.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Task;
import org.hl7.fhir.dstu3.model.Task.ParameterComponent;
import org.hl7.fhir.dstu3.model.Task.TaskRequesterComponent;
import org.hl7.fhir.dstu3.model.Task.TaskStatus;
import org.sitenv.dao.DBInterface;
import org.sitenv.dao.DBSchemaManager;
import org.sitenv.dao.derbyimpl.DBInterfaceImpl;
import org.sitenv.dao.derbyimpl.DBSchemaManagerImpl;
import org.sitenv.extract.EtlManager;
import org.sitenv.mapper.utilities.MapperConstants;
import org.sitenv.utilities.GenericUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;

public class WorkflowManager {
	
	static final Logger logger = LoggerFactory.getLogger(WorkflowManager.class);
	
	private static final String ID_SYSTEM = "DAF-Research";
	private static final String DAF_ETL_SYSTEM = "http://hl7.org/fhir/us/daf-research/OperationDefinition/";
	private static final String DAF_EXTRACT_CODE = "extract-operation";
	private static final String DAF_LOAD_CODE = "load-operation";
	private static final String DAF_EXTRACT_TASK_DESCRIPTION = "This is an extract operation";
	private static final String DAF_LOAD_TASK_DESCRIPTION = "This is a load operation";
	private static final String DAF_ORG_REFERENCE = "Organization/Daf-Research";
	private static final String DAF_REASON_SYSTEM = "http://hl7.org/fhir/us/daf-research/OperationDefinition/";
	private static final String DAF_EXTRACT_REASON_CODE = "extract-operation";
	private static final String DAF_LOAD_REASON_CODE = "load-operation";
	
	FhirContext ct;
	DBInterface dbInterface;
	DBSchemaManager schemaManager;
	EtlManager      etlManager;
	
	private String serverBase;
	private String patientListFile;
	private String dbUrl;
	private String serverName;
	
	public WorkflowManager(String sn, String sb, String plFile, String db, EtlManager etl) {
		serverName = sn;
		serverBase = sb;
		patientListFile = plFile;
		dbUrl = db;
		
		ct = FhirContext.forDstu3();
		
		dbInterface = (DBInterface) new DBInterfaceImpl(dbUrl);
		schemaManager = new DBSchemaManagerImpl(dbInterface);
		etlManager = etl;
	}
	
	public void initialize() {
		
		schemaManager.dropTables();
		schemaManager.createTables();
	}
	
	
	// Capabilty C1: Step 1
	public void createExtractionTask() {
		
		Task t = new Task();
		
		// Create Unique Id
		t.setId(GenericUtilities.getGuid());
		
		// Identifier
		List<Identifier> ids = new ArrayList<Identifier>();
		Identifier id = new Identifier();
		id.setSystem(ID_SYSTEM);
		id.setValue(GenericUtilities.getGuid());
		ids.add(id);
		t.setIdentifier(ids);
		
		// Status
		t.setStatus(TaskStatus.READY);
		
		// Code
		CodeableConcept cd = new CodeableConcept();
		Coding code = new Coding();
		code.setSystem(DAF_ETL_SYSTEM);
		code.setCode(DAF_EXTRACT_CODE);
		cd.addCoding(code);
		t.setCode(cd);
		
		// Description
		t.setDescription(DAF_EXTRACT_TASK_DESCRIPTION);
		
		// Authored on
		Date current = new Date();
		t.setAuthoredOn(current);
		
		// last modified
		t.setLastModified(current);
		
		// Requester
		TaskRequesterComponent tr = new TaskRequesterComponent();
		Reference ref = new Reference();
		tr.setAgent(ref);
		t.setRequester(tr);
		
		// Owner
		t.setOwner(ref);
		
		//Reason - Use the same as Task Code.
		t.setReason(cd);
		
		// Annotation -- Notes
		List<Annotation> ats = new ArrayList<Annotation>();
		Annotation at = new Annotation();
		at.setText(" This is for extraction ");
		ats.add(at);
		t.setNote(ats);
		
		// Inputs -- Need to ask James on how to setup Inputs with FHIR Data Types.
		
		
		// Initialize Outputs
		
		// Convert Resource to string to insert into Task
		String s = createSQLForTaskUpsert(t);
		if(s != null) {
			System.out.println(" Executing Task Insert ");
			logger.error("Successfully able to instantiate extraction task ");
			dbInterface.executeUpdate(s);
		}
		else {
			logger.error("Not able to instantiate extraction task ");
		}
		
	}
	
	
	public String createSQLForTaskUpsert(Task t) {
		
		if(t != null) {
	
			// Convert Resource to string to insert into Task
			String s = ct.newJsonParser().setPrettyPrint(true).encodeResourceToString(t);
	
			/*
			 * Need to construct an insert command as follows"
			 * INSERT into TASK VALUES 
			 * (ID, CLOB);
			 */
			String cmd = "insert into task values (";
			
			// Add Id
			cmd += MapperConstants.SINGLE_QUOTE + t.getId() + MapperConstants.SINGLE_QUOTE_COMMA;
			
			cmd += MapperConstants.SINGLE_QUOTE + s + MapperConstants.SINGLE_QUOTE;
			
			cmd += MapperConstants.END_PAREN;
			
			return cmd;
		}
				
		return null;
	}
	
	
	// Capabilty C1: Step 2
	public void executeExtractionTask() {
		
		if(etlManager != null) {
			
			etlManager.runExtract();
		}
		
	}
	
	// Capabilty C1: Step 3
	public void createLoadTask() {
		
		Task t = new Task();
		
		// Create Unique Id
		t.setId(GenericUtilities.getGuid());
		
		// Identifier
		List<Identifier> ids = new ArrayList<Identifier>();
		Identifier id = new Identifier();
		id.setSystem(ID_SYSTEM);
		id.setValue(GenericUtilities.getGuid());
		ids.add(id);
		t.setIdentifier(ids);
		
		// Status
		t.setStatus(TaskStatus.READY);
		
		// Code
		CodeableConcept cd = new CodeableConcept();
		Coding code = new Coding();
		code.setSystem(DAF_ETL_SYSTEM);
		code.setCode(DAF_LOAD_CODE);
		cd.addCoding(code);
		t.setCode(cd);
		
		// Description
		t.setDescription(DAF_LOAD_TASK_DESCRIPTION);
		
		// Authored on
		Date current = new Date();
		t.setAuthoredOn(current);
		
		// last modified
		t.setLastModified(current);
		
		// Requester
		TaskRequesterComponent tr = new TaskRequesterComponent();
		Reference ref = new Reference();
		tr.setAgent(ref);
		t.setRequester(tr);
		
		// Owner
		t.setOwner(ref);
		
		//Reason - Use the same as Task Code.
		t.setReason(cd);
		
		// Annotation -- Notes
		List<Annotation> ats = new ArrayList<Annotation>();
		Annotation at = new Annotation();
		at.setText(" This is for loading the data ");
		ats.add(at);
		t.setNote(ats);
		
		// Inputs -- Need to ask James on how to setup Inputs with FHIR Data Types.
		
		
		// Initialize Outputs
		
		// Convert Resource to string to insert into Task
		String s = createSQLForTaskUpsert(t);
		if(s != null) {
			System.out.println(" Executing Task Insert ");
			logger.error("Successfully able to instantiate load task ");
			dbInterface.executeUpdate(s);
		}
		else {
			logger.error("Not able to instantiate load task ");
		}
	}
	
	// Capabilty C1: Step 4
	public void executeLoadTask() {
		
		if(etlManager != null) {
			System.out.println(" Start executing transform and load task ");
			logger.debug(" Start executing transform and load task ");
			
			etlManager.runTransformAndLoad();
		}
		else {
			System.out.println(" Not able to run transform and load task ");
			logger.error(" Not able to run transform and load task");
		}
	}

}
