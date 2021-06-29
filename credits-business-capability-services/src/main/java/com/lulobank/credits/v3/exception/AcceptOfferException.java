package com.lulobank.credits.v3.exception;

public class AcceptOfferException extends RuntimeException {

	private static final long serialVersionUID = -1510461936019001462L;

	public AcceptOfferException(String message) {
		super(message);
	}

	public AcceptOfferException(String message, Throwable cause) {
		super(message, cause);
	}
}
