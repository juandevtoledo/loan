package com.lulobank.credits.v3.exception;

public class AcceptLoanTransactionException extends RuntimeException{
	
	private static final long serialVersionUID = 6600697035204136020L;
	
	public AcceptLoanTransactionException(String message) {
		super(message);
	}
	
	public AcceptLoanTransactionException(String message, Throwable cause) {
		super(message, cause);
	}
}
