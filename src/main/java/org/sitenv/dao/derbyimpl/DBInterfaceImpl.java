package org.sitenv.dao.derbyimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.sitenv.dao.DBInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBInterfaceImpl implements DBInterface {
	
	static final Logger logger = LoggerFactory.getLogger(DBInterfaceImpl.class);

	private String dbURL;
	private Connection conn;
	
	public DBInterfaceImpl(String dburl) {
		
		dbURL = dburl;
		initializeConnection();
	}
	
	public void initializeConnection() {
		
		try {
			conn = DriverManager.getConnection(dbURL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void executeStatement(String st) {
		
		try {
			Statement stmt = conn.createStatement();
			
			stmt.execute(st);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void executeUpdate(String st) {
	
		logger.debug(" Executing Update for String " + st);
		try {
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate(st);
			
			
		} catch (SQLException e) {
			
			logger.error("Caught Exception");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
