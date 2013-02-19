/*
 *-------------------------------------------------------------------
 *  The sample contained herein is provided to you "AS IS".
 *	The code has been significantly redacted.
 *  Reliability, maintainability and functionality cannot be guaranteed.
 *-------------------------------------------------------------------
 */
package com.chris.bit.eight.commerce.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;

import com.ibm.commerce.command.CommandFactory;
import com.ibm.commerce.command.ControllerCommandImpl;
import com.ibm.commerce.datatype.TypedProperty;
import com.ibm.commerce.ejb.helpers.ECConstants;
import com.ibm.commerce.exception.ECException;
import com.ibm.commerce.usermanagement.commands.UserRegistrationAddCmd;

/**
 * Implementation for server side Facebook log in and Facebook JavaScript SDK initiated log in
 * Provides registration for server side log in
 * JavaScript SDK registration is provided by {@link FacebookRegistrationCmdImpl}
 */
public class FacebookLogonCmdImpl extends ControllerCommandImpl implements
		FacebookLogonCmd {

	private static final String appId = "1234567890";
	private static final String secret = "3c77da06b80af137174d84f01035a80d";
	private static final String redirectUri = "https://localhost/webapp/wcs/stores/servlet/facebookLogon";
	private static final String storeView = "StoreView/10051";
	private static final String foobar = "0foobaabaz9";

	public void performExecute() throws ECException {
		super.performExecute();
		TypedProperty requestProperties = getRequestProperties();
		TypedProperty responseProperties = new TypedProperty();
		
		String accessToken = null;
		//Retrieve authorisation code from URL - server side flow
		String authCode = requestProperties.getString("code","");
		if (!authCode.isEmpty()) {
			//Build token exchange URL
			String tokenExchangeUrl = getTokenExchangeUrl(authCode);
			//Exchange code for access token
			accessToken = getAccessToken(tokenExchangeUrl);	
		} else {
			//Retrieve access token - log in flow
			accessToken = requestProperties.getString("accessToken","");
			if (accessToken.isEmpty()){
				//TODO Redacted
			} else {
			accessToken = accessToken.substring(accessToken.indexOf("="));
			}
		}
		//Get id from facebook
		String logonId = getLogonId(accessToken);
		
		Boolean register = loginUser(logonId);
		//If server side log in and user not registered
		if (register && !authCode.isEmpty()) registerUser(logonId);
		
		//Set the view
		responseProperties.put(ECConstants.EC_VIEWTASKNAME, storeView);
		setResponseProperties(responseProperties);		
	}
	
	private String getTokenExchangeUrl(String authCode) {
		String authUrl = null;
		try {
			authUrl = "https://graph.facebook.com/oauth/access_token?client_id="
					+ appId
					+ "&redirect_uri="
					+ URLEncoder.encode(redirectUri, "UTF-8")
					+ "&client_secret="
					+ secret 
					+ "&code=" 
					+ authCode;
		} catch (UnsupportedEncodingException e) {
			// TODO Redacted
		}
		return authUrl;
	}
	
	private String getAccessToken(String tokenExchangeUrl) {
	
		URL url = null;
		String accessToken = null;
		try {
			url = new URL(tokenExchangeUrl);
			String result = readResponse(url);
			String[] resultSplited = result.split("&");
			accessToken = resultSplited[0].split("=")[1];
		} catch (MalformedURLException e) {
			// TODO Redacted
		}
		return accessToken;
	}
		
	private String getLogonId(String accessToken) {
		String logonId = null;
		URL urler = null;
		JSONObject json = null;
		try {
			urler = new URL("https://graph.facebook.com/me?access_token=" + accessToken);
		} catch (MalformedURLException e) {
			// TODO Redacted
		}
		String results = readResponse(urler);
		//TODO Remove
		System.out.println(results);
		try {
			json = new JSONObject(results);
			logonId = json.getString("id");
		} catch (JSONException e) {
			// TODO Redacted
		}
	return logonId;
	}

	private String readResponse(URL url) {
		String result = "";
		try {
			InputStream inputStream = url.openStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = reader.readLine()) != null) {
				result += line;
			}
			reader.close();
		} catch (IOException e) {
			// TODO Redacted
		}
		return result;
	}
	
	private void registerUser(String logonId) throws ECException{
		//Create Madisons user
		UserRegistrationAddCmd userAdd = (UserRegistrationAddCmd) CommandFactory.createCommand(UserRegistrationAddCmd.class.getName(),getStoreId());
		userAdd.setCommandContext(getCommandContext());
		TypedProperty userAddProperties = new TypedProperty();
		userAddProperties.put("URL",foobar);
		userAddProperties.put("logonId",logonId);
		userAddProperties.put("logonPassword",foobar);
		userAddProperties.put("logonPasswordVerify",foobar);
		userAdd.setRequestProperties(userAddProperties);

		try {
			userAdd.execute();
		} catch (Exception e) {
			// TODO Redacted
		}
	}
	
	private Boolean loginUser(String logonId) throws ECException {
		//Extended log in
		Boolean register = false;
		FLogonCmd logon = (FLogonCmd) CommandFactory.createCommand(FLogonCmd.class.getName(),getStoreId());
		logon.setCommandContext(getCommandContext());
		logon.setRequestProperties(new TypedProperty());
		logon.setLogonId(logonId);
		//Redundant setters
		logon.setLogonPassword(foobar);
		logon.setPostLogonURL(foobar);
		logon.setReLogonURL(foobar);
		try {
			logon.execute();
		} catch (Exception e) {
			// TODO Redacted
			register = true;
		}
		return register;
	}
}