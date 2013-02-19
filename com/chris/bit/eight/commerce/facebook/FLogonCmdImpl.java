/*
 *-------------------------------------------------------------------
 *  The sample contained herein is provided to you "AS IS".
 *	The code has been significantly redacted.
 *  Reliability, maintainability and functionality cannot be guaranteed.
 *-------------------------------------------------------------------
 */
package com.chris.bit.eight.commerce.facebook;

import com.ibm.commerce.exception.ECException;
import com.ibm.commerce.security.commands.LogonCmdImpl;

/**
 * Logon extension overrides the isValidCredentials method to always return true.
 */
public class FLogonCmdImpl extends LogonCmdImpl implements FLogonCmd {
	
	@Override
	protected boolean isValidCredentials() throws ECException {
		return true;
	}

}
