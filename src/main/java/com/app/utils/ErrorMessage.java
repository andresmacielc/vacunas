package com.app.utils;

public class ErrorMessage {
	private String message;
	private Throwable cause;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Throwable getCause() {
		return cause;
	}
	public void setCause(Throwable throwable) {
		this.cause = throwable;
	}
	
	
}
