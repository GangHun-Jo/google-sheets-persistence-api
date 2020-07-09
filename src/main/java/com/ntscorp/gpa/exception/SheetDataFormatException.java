package com.ntscorp.gpa.exception;

public class SheetDataFormatException extends RuntimeException {
	public SheetDataFormatException(String msg, Throwable cause) {
		super(msg);
		super.initCause(cause);
	}
	public SheetDataFormatException(String msg) {
		super(msg);
	}
}
