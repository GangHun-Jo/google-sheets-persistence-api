package com.ntscorp.gpa.exception;

public class GoogleSheetConnectionException extends RuntimeException {
	public GoogleSheetConnectionException(String msg, Throwable cause) {
		super(msg);
		super.initCause(cause);
	}

	public GoogleSheetConnectionException(String msg) {
		super(msg);
	}
}
