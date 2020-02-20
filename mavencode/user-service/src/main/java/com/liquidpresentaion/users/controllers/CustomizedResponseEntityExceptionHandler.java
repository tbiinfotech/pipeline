package com.liquidpresentaion.users.controllers;

import java.util.Date;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.liquidpresentaion.users.exceptions.DuplicateEntityException;
import com.liquidpresentaion.users.exceptions.MaxNumberOfUsersExceededException;
import com.liquidpresentation.common.exceptions.ErrorDetails;

@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		
		logger.error("handleAllExceptions: " + ex);;
		
		String errorMessage = ex.getMessage();
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		
		if (ex.getCause() instanceof ConstraintViolationException) {
			String message = ex.getCause().getCause().getMessage();
			if (message.contains("duplicate key value violates unique constraint")) {
				httpStatus = HttpStatus.BAD_REQUEST;
				if (message.contains("u_email")) {
					errorMessage = "There already is user with same email!";
				} else {
					errorMessage = "There is already an entity with the same name!";
				}
			}
		}
		ErrorDetails errorDetails = new ErrorDetails(new Date(), errorMessage, request.getDescription(false));
		return new ResponseEntity(errorDetails, httpStatus);
	}

	@ExceptionHandler(MaxNumberOfUsersExceededException.class)
	public final ResponseEntity<Object> handleMaxNumberOfUsersExceededException(MaxNumberOfUsersExceededException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateEntityException.class)
	public final ResponseEntity<Object> handleDuplicateEntityException(DuplicateEntityException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), "Validation Failed: " + ex.getMessage(), ex.getBindingResult().toString());
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
