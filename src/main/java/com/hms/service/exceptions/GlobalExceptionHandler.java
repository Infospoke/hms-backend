package com.hms.service.exceptions;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAllUncaughtExceptions(Exception ex) {
		log.error("Exception occurred: {}", ex.getMessage());

		return new ResponseEntity<>(
				ApiResponse.failure(ResponseCode.FAILURE, List.of("Something went wrong: " + ex.getMessage())),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleValidationExceptions(ConstraintViolationException ex) {

		List<String> errors = ex.getConstraintViolations().stream().map(v -> v.getMessage()).toList();

		log.error("Validation error: {}", errors);

		return new ResponseEntity<>(ApiResponse.failure(ResponseCode.FAILURE, errors), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<?> handleMultiPartExceptions(MissingServletRequestPartException ex) {

		log.error("Multipart error: {}", ex.getMessage());

		return new ResponseEntity<>(ApiResponse.failure(ResponseCode.FAILURE, List.of(ex.getMessage())),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<?> entityNotFoundExceptions(EntityNotFoundException ex) {

		log.error("Entity not found: {}", ex.getMessage());

		return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {

		log.error("Illegal argument: {}", ex.getMessage());

		return new ResponseEntity<>(ApiResponse.failure(ResponseCode.FAILURE, List.of(ex.getMessage())),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<?> handleDuplicate(DuplicateException ex) {

		log.error("Duplicate error: {}", ex.getMessage());

		return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(CustomSystemErrorException.class)
	public ResponseEntity<?> handleCustomSystemErrors(CustomSystemErrorException ex) {

		log.error("System error: {}", ex.getMessage());

		return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {

		String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

		return new ResponseEntity<>(ApiResponse.failure(errorMessage), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException ex) {

		log.error("Already exists: {}", ex.getMessage());

		return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<?> handleOperationNotAllowedException(OperationNotAllowedException ex) {

		log.error("Operation not allowed: {}", ex.getMessage());

		return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException ex) {

		log.error("BadRequestException : {}", ex.getMessage());
		return new ResponseEntity<>(ApiResponse.failure(ResponseCode.FAILURE, List.of(ex.getMessage())),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {

		log.error("ResourceNotFoundException : {}", ex.getMessage());
		return new ResponseEntity<>(ApiResponse.failure(ResponseCode.FAILURE, List.of(ex.getMessage())),
				HttpStatus.NOT_FOUND);
	}
}
