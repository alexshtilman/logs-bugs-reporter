/**
 * 
 */
package telran.logs.bugs.controllers;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;

/**
 * @author Alex Shtilman Feb 28, 2021
 *
 */
@Log4j2
public class GlobalExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String constraintViolationHandler(ConstraintViolationException e) {
		return processException(e);
	}

	@ExceptionHandler(DuplicatedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String duplicatedKeyHandler(DuplicatedException e) {
		return processException(e);
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFoundHandler(NotFoundException e) {
		return processException(e);
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String serverHandler(RuntimeException e) {
		return processException(e);
	}

	private String processException(Exception exception) {
		log.debug("exception class: {}, message: {}", exception.getClass(), exception.getMessage());
		return exception.getMessage();
	}
}
