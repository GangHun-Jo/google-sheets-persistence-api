package com.ntscorp.gpa.exception;

public class SheetDataMappingException extends RuntimeException {
	public SheetDataMappingException(String msg, Throwable cause) {
		super(msg);
		super.initCause(cause);
	}

	public SheetDataMappingException(String msg) {
		super(msg);
	}
}
