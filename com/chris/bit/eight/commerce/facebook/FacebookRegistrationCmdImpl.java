/*
 *-------------------------------------------------------------------
 *  The sample contained herein is provided to you "AS IS".
 *	The code has been significantly redacted.
 *  Reliability, maintainability and functionality cannot be guaranteed.
 *-------------------------------------------------------------------
 */
package com.chris.bit.eight.commerce.facebook;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;

import com.ibm.commerce.command.CommandFactory;
import com.ibm.commerce.command.ControllerCommandImpl;
import com.ibm.commerce.command.ViewCommandContext;
import com.ibm.commerce.datatype.TypedProperty;
import com.ibm.commerce.ejb.helpers.ECConstants;
import com.ibm.commerce.exception.ECException;
import com.ibm.commerce.usermanagement.commands.UserRegistrationAddCmd;
import com.ibm.commerce.webcontroller.HttpControllerRequestObject;

/**
 * JavaScript SDK initiated registration
 */
public class FacebookRegistrationCmdImpl extends ControllerCommandImpl implements
		FacebookRegistrationCmd {

	private static final String storeView = "StoreView/10051";
	private static final String foobar = "0foobaabaz9";

	public void performExecute() throws ECException {
		super.performExecute();
		
		String signedRequest = getSignedRequest();
		//Signature and payload are separated by a .
        String[] signedRequestSplit = signedRequest.split("\\.");
        isSignatureValid(signedRequestSplit[0]);
        String data = getDecodedString(signedRequestSplit[1]);
        String[] registrationFields = getRegistrationFields(data);
       
		//Create Madison user
		UserRegistrationAddCmd userAdd = (UserRegistrationAddCmd) CommandFactory.createCommand(UserRegistrationAddCmd.class.getName(),getStoreId());
		userAdd.setCommandContext(getCommandContext());
		TypedProperty userAddProperties = new TypedProperty();
		userAddProperties.put("URL",foobar);
		userAddProperties.put("logonId",registrationFields[0]);
		userAddProperties.put("logonPassword",foobar);
		userAddProperties.put("logonPasswordVerify",foobar);
		userAddProperties.put("firstName",registrationFields[1]);
		userAddProperties.put("lastName",registrationFields[2]);
		userAddProperties.put("email1",registrationFields[3]);
		userAddProperties.put("address1",registrationFields[4]);
		userAddProperties.put("city",registrationFields[5]);
		userAddProperties.put("state",registrationFields[6]);
		userAddProperties.put("zipCode",registrationFields[7]);
		userAddProperties.put("country",registrationFields[8]);
		userAdd.setRequestProperties(userAddProperties);

		try {
			userAdd.execute();
		} catch (Exception e) {
			// TODO Redacted
		}
		
		TypedProperty responseProperties = new TypedProperty();
		responseProperties.put(ECConstants.EC_VIEWTASKNAME, "StoreView/10051");
		setResponseProperties(responseProperties);
	}

    /**
     * Extract registration details from data
     * 
     * @param data {@link String}
     * @return
     */
    private String[] getRegistrationFields(String data) {
    	JSONObject jsonRoot = null;
		try {
			jsonRoot = new JSONObject(data);
		} catch (JSONException e) {
			//TODO Redacted
		}
        
		String logonId = null;
		String name = null;
		String email = null;
		String zipCode = null;
		String street = null;
		String cityCountry = null;
		String stateCountry = null;
		try {
			logonId = jsonRoot.getString("user_id");
			JSONObject jsonChild = jsonRoot.getJSONObject("registration");
			name = jsonChild.getString("name");
			email = jsonChild.getString("email");
			street = jsonChild.getString("street");
			cityCountry = jsonChild.getJSONObject("city").getString("name");
			stateCountry = jsonChild.getJSONObject("state").getString("name");
			zipCode = jsonChild.getString("zip");
		} catch (JSONException e) {
			//TODO Redacted
		}
		
		String[] nameSplit = name.split(" ");
		String firstName = nameSplit[0];
		String lastName = nameSplit[1];
		String[] cityCountrySplit = cityCountry.split(",");
		String city = cityCountrySplit[0];
		String[] stateCountrySplit = stateCountry.split(",");
		String state = stateCountrySplit[0];
		String country = stateCountrySplit[1].trim();
		
		String[] registrationFields = {logonId, firstName, lastName, email, street, city, state, zipCode, country};
		
		return registrationFields;
	}

	/**
     * Validate the signature
     * 
     * @param encodedSignature {@link String}
     * @return {@link Boolean}
     */
    private boolean isSignatureValid(String encodedSignature) {
    	//TODO Redacted
		return true;
    }
	
	/**
	 * Decode a base 64 encoded String
	 * 
	 * @param encodedString {@link String}
	 * @return {@link String}
	 */
	private String getDecodedString(String encodedString) {
		Base64 base64 = new Base64();
		String decodedString = null;
		try {
			decodedString = new String(base64.decode(encodedString.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Redacted
		}
		return decodedString;
	}

    /**
     * Obtain the signed_request from the HTTP request
     * 
     * @return {@link String}
     */
    private String getSignedRequest() {
    	HttpServletRequest httpRequest = ((HttpControllerRequestObject) ((ViewCommandContext) getCommandContext()).getRequest()).getHttpRequest();
		String signedRequest = httpRequest.getParameter("signed_request");
		return signedRequest;
    }

}