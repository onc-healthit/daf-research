package org.sitenv.dao.derbyimpl;

import org.sitenv.dao.DBInterface;
import org.sitenv.dao.DBSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBSchemaManagerImpl implements DBSchemaManager {
	
	static final Logger logger = LoggerFactory.getLogger(DBSchemaManagerImpl.class);

	DBInterface db;
	
	public DBSchemaManagerImpl(DBInterface dbi) {
		db = dbi;
	}

	public void createTables() {
		
		String patcrt = "Create Table DEMOGRAPHIC ( PATID varchar(100) NOT NULL CONSTRAINT PATID_pk primary key, BIRTH_DATE DATE, BIRTH_TIME TIME, SEX varchar(2), HISPANIC varchar(2), RACE varchar(2), BIOBANK_FLAG char)";
		String condcrt = "Create Table CONDITION ( CONDITIONID varchar(100) primary key, PATID varchar(100) CONSTRAINT PATID_FK references DEMOGRAPHIC (PATID), ENCOUNTERID varchar(100), REPORT_DATE DATE, RESOLVE_DATE DATE, ONSET_DATE DATE, CONDITION_STATS varchar(2), CONDITION varchar(18), CONDITION_TYPE varchar(2), CONDITION_SOURCE varchar(2))";
		String taskcrt = "Create Table TASK ( ID varchar(100) primary key, data clob (64 K))";
		
		db.executeStatement(patcrt);
		db.executeStatement(condcrt);
		db.executeStatement(taskcrt);
		
	 
	}
	
	public void dropTables() {
		
		
		String patdrp = "Drop Table DEMOGRAPHIC";
		String conddrp = "Drop Table CONDITION";
		String taskdrp = "Drop Table TASK";
		
		db.executeStatement(taskdrp);
		db.executeStatement(conddrp);
		db.executeStatement(patdrp);
	}
}
