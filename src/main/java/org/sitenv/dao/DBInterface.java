package org.sitenv.dao;

public interface DBInterface {

	public void initializeConnection();
	
	public void closeConnection();
	
	public void executeStatement(String s);
	
	public void executeUpdate(String s);
}
