package sanko.sankons.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*; //RestControllerAdvice, ExceptionHandler
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.http.*; //ResponseEntity, HttpStatus

import sanko.sankons.web.dto.MessageResponse;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MessageResponse> handleValidationError(MethodArgumentNotValidException e) {
		List<String> errors = e.getFieldErrors()
			.stream()
			.map(FieldError::getDefaultMessage)
			.collect(Collectors.toList());
		String message = String.join(", ", errors);
		return new ResponseEntity<>(
			new MessageResponse(message),
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<MessageResponse> handleError(Exception e) {
		return new ResponseEntity<>(
			new MessageResponse(e.getMessage()),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

}
