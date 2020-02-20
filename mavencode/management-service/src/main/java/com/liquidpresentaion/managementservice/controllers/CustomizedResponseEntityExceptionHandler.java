package com.liquidpresentaion.managementservice.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.liquidpresentation.common.exceptions.ErrorDetails;
import com.sun.mail.util.MailConnectException;

@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

	@Value("${spring.servlet.multipart.max-file-size}")
	private String maxFileSize;
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MultipartException.class)
	public final ResponseEntity<Object> handleSizeLimitExceededException(Exception ex, WebRequest request) {
		String message = ex.getMessage();
		if (message.contains("SizeLimitExceededExceptio")) {
			message = "Maximum upload size [" + maxFileSize + "] exceeded!";
		}
		ErrorDetails errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MailConnectException.class)
	public final ResponseEntity<Object> handleMailConnectException(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), "Mail server connection failed! Please check your email settings.", request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), "Validation Failed: " + ex.getMessage(), ex.getBindingResult().toString());
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
