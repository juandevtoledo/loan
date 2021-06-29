package com.lulobank.credits.services.exceptions;

import lombok.Getter;

@Getter
public class RiskEngineException extends  Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4666416471345683608L;
	private final int code;
    private final String message;
    public RiskEngineException(int code,String message) {
        super(message);
        this.code = code;
        this.message=message;
    }
}
