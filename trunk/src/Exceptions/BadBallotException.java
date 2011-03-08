package Exceptions;

import java.io.IOException;

public class BadBallotException extends IOException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadBallotException(String crushStr){
		super(crushStr);
	}

}
