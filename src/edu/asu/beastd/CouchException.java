package edu.asu.beastd;

public class CouchException extends RuntimeException {

	private static final long serialVersionUID = 8531233448872061090L;

	public CouchException(String message) {
		super(message);
	}

	public CouchException(Throwable cause) {
		super(cause);
	}

	public CouchException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouchException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
