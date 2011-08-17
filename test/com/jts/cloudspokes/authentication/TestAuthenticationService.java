package com.jts.cloudspokes.authentication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

/**
 * Test https://cloudspokes-sf-authentication.appspot.com/ 
 * @author sinduja
 *
 */

public class TestAuthenticationService {
	
	private String username = "sinduja";
	
	private String password = "cspokes007";
	
	private String securityToken = "xi3ODF5HmsiiYVYz7Ey5DZKqo";
	
	private String SERVER_URL = "https://cloudspokes-sf-authentication.appspot.com/authenticate";

	@Test
	public void TestServiceWithNoData() throws Exception {
		String response = makePostRequest(SERVER_URL, "");
		JSONObject responseObj = (JSONObject) JSONValue.parse(response);
		Assert.assertEquals(CAConstants.STATUS_FAILURE, responseObj.get(CAConstants.STATUS));
		Assert.assertEquals(CAConstants.VALIDATION_ERROR, responseObj.get(CAConstants.TYPE));
	}
	
	@Test
	public void TestServiceInvalidCredentials() throws Exception {
		String response = makePostRequest(SERVER_URL, "username=username&password=password");
		JSONObject responseObj = (JSONObject) JSONValue.parse(response);
		Assert.assertEquals(CAConstants.STATUS_FAILURE, responseObj.get(CAConstants.STATUS));
		Assert.assertEquals(CAConstants.SERVER_ERROR, responseObj.get(CAConstants.TYPE));
	}
	
	@Test
	public void TestServiceWithValidData() throws Exception {
		String response = makePostRequest(SERVER_URL, "username=" + username + "&password=" + password + securityToken);
		JSONObject responseObj = (JSONObject) JSONValue.parse(response);
		Assert.assertEquals(CAConstants.STATUS_SUCCESS, responseObj.get(CAConstants.STATUS));
		Object responseData = responseObj.get(CAConstants.RESPONSE);
		Assert.assertNotNull(responseData);
		JSONObject responseJson = (JSONObject) responseData;
		Assert.assertNotNull(responseJson.get("access_token"));
		String userData = makeGetRequest((String) responseJson.get("access_token"), (String) responseJson.get("id"));
		Assert.assertNotNull(userData);
		JSONObject userJson = (JSONObject) JSONValue.parse(userData);
		Assert.assertEquals(true, userJson.get("asserted_user"));
	}
	
	private String makePostRequest(String url, String postData) throws MalformedURLException, IOException {
		URL endpoint = new URL(url);
		HttpURLConnection urlc = (HttpURLConnection) endpoint.openConnection();
		urlc.setRequestMethod("POST");
		urlc.setDoOutput(true);
		
		DataOutputStream dos = new DataOutputStream(urlc.getOutputStream());
		dos.writeBytes(postData);
		dos.flush();
		dos.close();
		
		String output = readInputStream(urlc.getInputStream());
		urlc.disconnect();
		return output;
	    
	}
	
	private String makeGetRequest(String accessToken, String url) throws MalformedURLException, IOException {
		URL endpoint = new URL(url);
		HttpURLConnection urlc = (HttpURLConnection) endpoint.openConnection();
		urlc.setRequestProperty("Authorization", "OAuth " + accessToken);
		urlc.setRequestMethod("GET");
		urlc.setDoOutput(true);
		
		String output = readInputStream(urlc.getInputStream());
		urlc.disconnect();
		return output;
	    
	}
	
	private String readInputStream(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    String line = reader.readLine();
	    while (line != null) {
	    	sb.append(line + "\n");
	    	line = reader.readLine();
	    }
	    inputStream.close();
		return sb.toString().trim();
	}
}
