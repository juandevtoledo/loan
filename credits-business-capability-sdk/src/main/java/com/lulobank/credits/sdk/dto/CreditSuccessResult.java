package com.lulobank.credits.sdk.dto;

import java.io.Serializable;

public class CreditSuccessResult<T> implements CreditResult, Serializable {

	private static final long serialVersionUID = 1L;

	private T content;

	public CreditSuccessResult() {

	}

	public CreditSuccessResult(T content) {
		this.content = content;
	}

	public T getContent() {
		return content;
	}
}
