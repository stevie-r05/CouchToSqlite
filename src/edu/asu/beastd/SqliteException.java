package edu.asu.beastd;

public class SqliteException extends RuntimeException {

	private static final long serialVersionUID = 8286785776245596050L;

	public SqliteException(String message) {
		super(message);
	}

	public SqliteException(Throwable cause) {
		super(cause);
	}

	public SqliteException(String message, Throwable cause) {
		super(message, cause);
	}

	public SqliteException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
