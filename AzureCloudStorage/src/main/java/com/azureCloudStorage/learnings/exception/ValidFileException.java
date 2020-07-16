package com.azureCloudStorage.learnings.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ValidFileException extends RuntimeException {

	public ValidFileException(String message) {
		super(message);
	}
}