package Exceptions;

import java.io.IOException;

public class NoConnectionException extends IOException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoConnectionException(String crushString) {
		super(crushString);
	}
}
