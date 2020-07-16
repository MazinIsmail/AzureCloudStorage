package com.azureCloudStorage.learnings.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<?> handleMultipartException(Exception ex) {

		return new ResponseEntity<String>("File Size Exceeded 25MB",
				HttpStatus.EXPECTATION_FAILED);
	}

	@ExceptionHandler(MyFileNotFoundException.class)
	public ResponseEntity<?> handleFileNotFoundException(
			MyFileNotFoundException ex) {

		return new ResponseEntity<String>("File not found",
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ValidFileException.class)
	public ResponseEntity<?> handleValidFileException(
			ValidFileException ex) {
		logger.error("Error!!!! Failed to upload,CAUSE:{} {}", ex, ex.getStackTrace());
		return new ResponseEntity<String>("Failed",
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<?> handleFileStorageException(
			FileStorageException ex) {
		logger.error("Error!!!! Failed to upload,CAUSE:{} {}", ex, ex.getStackTrace());
		return new ResponseEntity<String>("File Upload Failed",
				HttpStatus.NOT_FOUND);
	}

}
