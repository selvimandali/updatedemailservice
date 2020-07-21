package com.ent.qbthon.email.exception;

public class EmailServiceException extends RuntimeException {

	public EmailServiceException() {
	}

private static final long serialVersionUID = 1L;
	
	public EmailServiceException(String errorMessage, Throwable t) {
		super(errorMessage, t);
	}

	public EmailServiceException(String errorMessage) {
		super(errorMessage);
	}

}
