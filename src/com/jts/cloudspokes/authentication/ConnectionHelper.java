package com.jts.cloudspokes.authentication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

/**
 * Connection utility class 
 * @author sinduja
 *
 */

public class ConnectionHelper {
	
	private static final Logger logger = Logger.getLogger(ConnectionHelper.class.getName());
	
	/**
	 * Appends json object key and value for url parameter 
	 * @param paramObj {@link JSONObject} contains key value pair of parameter and value  
	 * @return {@link String} appended key value parameters
	 */
	@SuppressWarnings("unchecked")
	public static String constructUrlParameters(JSONObject paramObj) {
		Iterator<String> iterator = paramObj.keySet().iterator();
		String paramStr = "";
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = paramObj.get(key);
			paramStr += "&" + key + "=" + value;
		}
		return paramStr.replaceFirst("&", "");
	}
	
	/**
	 * Establishes connection to server and makes post request.
	 * Reads the response and returns as string
	 * @param url {@link String} server url
	 * @param urlParameters {@link String} data to be posted
	 * @return {@link String} server response
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String makePostRequest(String url, String urlParameters) throws MalformedURLException, IOException {
		URL targetUrl = new URL(url);
		
		HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
		  
			
		connection.setDoOutput(true);
		DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
		dos.writeBytes(urlParameters);
		dos.flush();
		dos.close();
		
		String output = readInputStream(connection.getInputStream());
		connection.disconnect();
		return output;
	}
	
	/**
	 * Reads the string from InputStream  
	 * @param input {@link InputStream}
	 * @return {@link String} data from Stream
	 */
	public static String readInputStream(InputStream input) {
	    StringBuilder sb = new StringBuilder();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

	    try {
	    	String line = reader.readLine();
	    	while (line != null) {
	    		sb.append(line + "\n");
	    		line = reader.readLine();
	    	}
	    } catch (IOException e) {
	    	logger.severe("Error reading input stream=" + e.toString());
	    } finally {
		      try {
		    	  input.close();
		      } catch (IOException e) {
		    	  logger.severe("Error reading input stream=" + e.toString());
		      }
	    }
	    return sb.toString().trim();
	}
}
