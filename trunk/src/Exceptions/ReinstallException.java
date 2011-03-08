package Exceptions;

import java.security.GeneralSecurityException;

public class ReinstallException extends GeneralSecurityException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReinstallException(String crushString) {
		super(crushString);
	}
}
