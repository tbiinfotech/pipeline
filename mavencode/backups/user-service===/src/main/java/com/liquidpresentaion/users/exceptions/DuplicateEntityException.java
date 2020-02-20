package com.liquidpresentaion.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateEntityException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2801965672902354266L;

	public DuplicateEntityException(String message){
		super(message);
	}
}
