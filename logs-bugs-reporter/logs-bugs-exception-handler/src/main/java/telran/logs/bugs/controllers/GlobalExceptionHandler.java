/**
 * 
 */
package telran.logs.bugs.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;

/**
 * @author Alex Shtilman Feb 28, 2021
 *
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = { ConstraintViolationException.class, NumberFormatException.class,
			MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String requestFormatHandler(Exception ex) {
		return processingExceptions(ex);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String requestNotValidExceptionHandler(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = (error instanceof FieldError) ? ((FieldError) error).getField() : "<common>";
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		String result = "{'MethodArgumentNotValidException':" + convertWithStream(errors) + "}";
		log.error(result);
		return result;
	}

	@ExceptionHandler(DuplicatedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String duplicatedKeyHandler(DuplicatedException e) {
		return processingExceptions(e);
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFounHandler(NotFoundException e) {
		return processingExceptions(e);
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String serverExceptionHandler(RuntimeException e) {
		return processingExceptions(e);
	}

	private String processingExceptions(Exception e) {
		log.error("{}, message: {}", e.getClass().getSimpleName(), e.getMessage());
		return e.getMessage();
	}

	public String convertWithStream(Map<String, String> map) {
		return map.keySet().stream().map(key -> "'" + key + "':'" + map.get(key) + "'")
				.collect(Collectors.joining(", ", "{", "}"));
	}
}
