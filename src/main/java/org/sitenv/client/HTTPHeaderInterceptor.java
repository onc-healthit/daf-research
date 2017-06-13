package org.sitenv.client;

import java.io.IOException;

import ca.uhn.fhir.rest.client.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class HTTPHeaderInterceptor implements IClientInterceptor  {
	
	public void interceptRequest(IHttpRequest req) {
		 
		// Add return format
		req.addHeader("Content-Type", "application/json+fhir");
		
		// Add Accept header
	}

	public void interceptResponse(IHttpResponse resp) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
