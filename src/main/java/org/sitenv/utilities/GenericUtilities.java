package org.sitenv.utilities;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericUtilities {
	
	static final Logger logger = LoggerFactory.getLogger(GenericUtilities.class);

	public static String getGuid() {
		return UUID.randomUUID().toString(); 
	}
	
}
