package com.jts.cloudspokes.authentication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This servler implements SalesForce OAuth 2.0 Username Password Flow
 * 
 * Post request server with username and password parameters
 * Password is password + security_token
 * The server inturn with required credentials contacts SalesForce OAuther Server for access token
 * This access token can be used for further interactions with the SalesForce server
 * 
 * @author sinduja
 */

@SuppressWarnings("serial")
public class CloudspokesAuthenticationComponentServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(CloudspokesAuthenticationComponentServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String username = req.getParameter(OAuth.USERNAME);
		String password = req.getParameter(OAuth.PASSWORD); //password appended with security token
		
		//prepare response
		res.setContentType("text/plain");
		JSONObject resultJSON = new JSONObject();
		
		//validate username and password
		if (isBlank(username) || isBlank(password)) {
			resultJSON.put(CAConstants.STATUS, CAConstants.STATUS_FAILURE);
			resultJSON.put(CAConstants.TYPE, CAConstants.VALIDATION_ERROR);
			resultJSON.put(CAConstants.RESPONSE, "Invalid username/password");
		} else {
			try {
				//construct request data
				JSONObject reqParamObj = new JSONObject();
				reqParamObj.put(OAuth.GRANT_TYPE, OAuth.GRANT_TYPE_PASSWORD);
				reqParamObj.put(OAuth.CLIENT_ID, CAConstants.CONSUMER_KEY);
				reqParamObj.put(OAuth.CLIENT_SECRET, CAConstants.CONSUMER_SECRET);
				reqParamObj.put(OAuth.USERNAME, username + CAConstants.USERNAME_SUFFIX);
				reqParamObj.put(OAuth.PASSWORD, password);
				
				//make request
				String responseString = ConnectionHelper.makePostRequest(CAConstants.URL_AUTH_ENDPOINT, ConnectionHelper.constructUrlParameters(reqParamObj));
				logger.info("Authorization response " + responseString);
				
				//process response and check for error
				JSONObject responseJSON = (JSONObject) JSONValue.parse(responseString);
				System.out.println(responseString.toString());
				if (responseJSON.get(CAConstants.ERROR) != null || responseJSON.get(OAuth.ACCESS_TOKEN) == null) { //error
					resultJSON.put(CAConstants.STATUS, CAConstants.STATUS_FAILURE);
					resultJSON.put(CAConstants.TYPE, CAConstants.SERVER_ERROR);
					resultJSON.put(CAConstants.RESPONSE, responseJSON);
				} else { //success
					resultJSON.put(CAConstants.STATUS, CAConstants.STATUS_SUCCESS);
					resultJSON.put(CAConstants.RESPONSE, responseJSON);
				}
			} catch(Exception e) {
				logger.log(Level.SEVERE, e.getMessage());			
			}			
		}
		
		//send back response
		res.getWriter().write(resultJSON.toString());
	}
	
	private boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}
}
